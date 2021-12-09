package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.PasswordMismatchException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.PasswordService;
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
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/v1/authentification", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final ReCaptchaService reCaptchaService;

    private final EtablissementService etablissementService;

    private final EmailService emailService;

    private final UserDetailsServiceImpl userService;

    private final PasswordService passwordService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider,
                                    ReCaptchaService reCaptchaService,
                                    EtablissementService etablissementService,
                                    EmailService emailService,
                                    UserDetailsServiceImpl userService,
                                    PasswordService passwordService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = jwtTokenProvider;
        this.reCaptchaService = reCaptchaService;
        this.etablissementService = etablissementService;
        this.emailService = emailService;
        this.userService = userService;
        this.passwordService = passwordService;
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
    public ResponseEntity<?> resetPassword(@Valid @RequestBody MotDePasseOublieRequestDto dto, HttpServletRequest request) throws RestClientException, CaptchaException, JsonIncorrectException {
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        String captcha = dto.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException("Le champs 'recaptcha' est obligatoire");
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, ReCaptchaAction.MOT_DE_PASSE_OUBLIE);
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        UserDetailsImpl user;

        if (dto.getEmail() != null) {
            try {
                user = (UserDetailsImpl)userService.loadUser(etablissementService.getUserByMail(dto.getEmail()));

            } catch (IllegalArgumentException ex) {
                throw new UsernameNotFoundException("L'utilisateur avec l'email '" + dto.getEmail() + "' n'existe pas");
            }
        }
        else if (dto.getSiren() != null) {
            try {
                user = (UserDetailsImpl)userService.loadUser(etablissementService.getFirstBySiren(dto.getSiren()));
            } catch (IllegalArgumentException ex) {
                throw new UsernameNotFoundException("L'utilisateur avec le SIREN '" + dto.getSiren() + "' n'existe pas");
            }
        } else {
            throw new JsonIncorrectException("Au moins un des champs 'siren' ou 'email' est obligatoire");
        }

        String jwt = tokenProvider.generateToken(user);
        emailService.constructResetTokenEmail(locale, jwt, user.getEmail(), user.getNameEtab());

        MotDePasseOublieResponsetDto response = new MotDePasseOublieResponsetDto();
        response.setMessage("Un mail avec un lien de réinitialisation vous a été envoyé");

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping(value = "/reinitialiserMotDePasse")
    public ResponseEntity<?> resetPasswordConfirm(@Valid @RequestBody ReinitialiserMotDePasseRequestDto request) throws CaptchaException, InvalidTokenException, JsonIncorrectException, MailDoublonException, SirenExistException {
        String captcha = request.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException("Le champs 'recaptcha' est obligatoire");
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, ReCaptchaAction.REINITIALISER_MOT_DE_PASSE);
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
            EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
            ContactEntity contact = etab.getContact();
            contact.setMotDePasse(request.getMotDePasse());
            etablissementService.save(etab);
        } else {
            throw new InvalidTokenException("Le token n'est pas valide");
        }

        ReinitialiserMotDePasseResponseDto response = new ReinitialiserMotDePasseResponseDto();
        response.setMessage("Votre mot de passe a bien été réinitialisé");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifierValiditeToken")
    public ResponseEntity<?> verifierValiditeToken(@Valid @RequestBody VerifierValiditeTokenRequestDto request) throws JsonIncorrectException {

        if (request.getToken() == null) {
            throw new JsonIncorrectException("Le champs 'token' est obligatoire");
        }

        VerifierValiditeTokenResponseDto response = new VerifierValiditeTokenResponseDto();

        try {

            if (tokenProvider.validateToken(request.getToken())) {
                response.setValid(true);
            } else {
                response.setValid(false);
            }
        } catch (ExpiredJwtException ex) {
            response.setValid(false);
        }

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "permet de mettre à jour le mot de passe une fois connecté")
    @PostMapping(value = "/modifierMotDePasse")
    public ResponseEntity<?> modifierMotDePasse(HttpServletRequest requestHtttp, @Valid @RequestBody ModifierMotDePasseRequestDto request) throws PasswordMismatchException, MailDoublonException, SirenExistException, JsonIncorrectException, InvalidTokenException {

        String siren = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(requestHtttp));

        if (request.getAncienMotDePasse() == null) {
            throw new JsonIncorrectException("Le champs 'ancienMotDePasse' est obligatoire");
        }

        if (request.getNouveauMotDePasse() == null) {
            throw new JsonIncorrectException("Le champs 'nouveauMotDePasse' est obligatoire");
        }

        String oldPassword = request.getAncienMotDePasse();
        String newPassword = request.getNouveauMotDePasse();

        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        ContactEntity contact = etab.getContact();

        if (passwordService.estLeMotDePasse(oldPassword, contact.getMotDePasse())) {
            if (!passwordService.estLeMotDePasse(newPassword, contact.getMotDePasse())) {
                contact.setMotDePasse(newPassword);
                etablissementService.save(etab);
            } else {
                throw new PasswordMismatchException("Votre nouveau mot de passe doit être différent de l'ancien");
            }
        } else {
            throw new PasswordMismatchException("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel.");
        }

        ModifierMotDePasseResponseDto response = new ModifierMotDePasseResponseDto();
        response.setMessage("Votre mot de passe a bien été modifié");
        return ResponseEntity.ok(response);
    }
}


