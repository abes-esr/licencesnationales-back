package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.dto.authentification.*;
import fr.abes.licencesnationales.web.exception.CaptchaException;
import fr.abes.licencesnationales.web.exception.InvalidTokenException;
import fr.abes.licencesnationales.web.exception.JsonIncorrectException;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaAction;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.validation.Valid;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1/authentification")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final ReCaptchaService reCaptchaService;

    private final EtablissementService etablissementService;

    private final EmailService emailService;

    private final UserDetailsServiceImpl userService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider,
                                    ReCaptchaService reCaptchaService,
                                    EtablissementService etablissementService,
                                    EmailService emailService,
                                    UserDetailsServiceImpl userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = jwtTokenProvider;
        this.reCaptchaService = reCaptchaService;
        this.etablissementService = etablissementService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @ApiOperation(value = "permet de s'authentifier et de récupérer un token.",
            notes = "le token doit être utilisé pour accéder aux ressources protegées.")
    @PostMapping("/connexion")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody ConnexionRequestDto loginRequest) {
        UsernamePasswordAuthenticationToken credential = new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(credential);

        if (authentication == null) {
            throw new AuthenticationServiceException("La méthode d'authentification n'est pas supportée");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //on charge dans user le tuple bdd mis dans authentication
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal(); //ici si mauvais credentials NestedServletException
        String jwt = tokenProvider.generateToken(user);

        ConnexionResponseDto response = new ConnexionResponseDto();
        response.setAccessToken(jwt);
        response.setUserId(user.getId());
        response.setUserNameEtab(user.getNameEtab());
        response.setUserSiren(user.getSiren());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);

    }

    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/motDePasseOublie")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody MotDePasseOublieRequestDto request) throws RestClientException, CaptchaException, JsonIncorrectException {

        String captcha = request.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException("Le champs 'recaptcha' est obligatoire");
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, ReCaptchaAction.MOT_DE_PASSE_OUBLIE);
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        UserDetailsImpl user;

        if (request.getEmail() != null) {
            try {
                user = (UserDetailsImpl) userService.loadUser(etablissementService.getUserByMail(request.getEmail()));
            } catch (IllegalArgumentException ex) {
                throw new UsernameNotFoundException("L'utilisateur avec l'email '" + request.getEmail() + "' n'existe pas");
            }
        } else if (request.getSiren() != null) {
            try {
                user = (UserDetailsImpl) userService.loadUser(etablissementService.getFirstBySiren(request.getSiren()));
            } catch (IllegalArgumentException ex) {
                throw new UsernameNotFoundException("L'utilisateur avec le SIREN '" + request.getSiren() + "' n'existe pas");
            }
        } else {
            throw new JsonIncorrectException("Au moins un des champs 'siren' ou 'email' est obligatoire");
        }

        String jwt = tokenProvider.generateToken(user);
        emailService.constructResetTokenEmail(jwt, user.getEmail(), user.getNameEtab());

        MotDePasseOublieResponsetDto response = new MotDePasseOublieResponsetDto();
        response.setMessage("Un mail avec un lien de réinitialisation vous a été envoyé");

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/reinitialiserMotDePasse")
    public ResponseEntity<?> enregistrerPassword(@Valid @RequestBody ReinitialiserMotDePasseRequestDto request) throws CaptchaException, InvalidTokenException, JsonIncorrectException {
        String captcha = request.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException("Le champs 'recaptcha' est obligatoire");
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, ReCaptchaAction.MOT_DE_PASSE_OUBLIE);
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        if (request.getTokenFromMail() == null) {
            throw new JsonIncorrectException("Le champs 'token' est obligatoire");
        }

        if (request.getMotDePasse() == null) {
            throw new JsonIncorrectException("Le champs 'nouveauMotDePasse' est obligatoire");
        }

        if (tokenProvider.validateToken(request.getTokenFromMail())) {
            String siren = tokenProvider.getSirenFromJwtToken(request.getTokenFromMail());
            etablissementService.changePasswordFromSiren(siren, request.getMotDePasse());
        } else {
            throw new InvalidTokenException("Le token n'est pas valide");
        }

        ReinitialiserMotDePasseResponseDto response = new ReinitialiserMotDePasseResponseDto();
        response.setMessage("Votre mot de passe a bien été réinitialiser");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifierValiditeToken")
    public ResponseEntity<?> verifierValiditeToken(@Valid @RequestBody VerifierValiditeTokenRequestDto request) throws JsonIncorrectException {

        if (request.getToken() == null) {
            throw new JsonIncorrectException("Le champs 'token' est obligatoire");
        }

        VerifierValiditeTokenResponseDto response = new VerifierValiditeTokenResponseDto();

        if (tokenProvider.validateToken(request.getToken())) {
            response.setValid(true);
        } else {
            response.setValid(false);
        }

        return ResponseEntity.ok(response);
    }
}


