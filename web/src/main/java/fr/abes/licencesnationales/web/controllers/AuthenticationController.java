package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.constant.Constant;
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
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/v1/authentification", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController extends AbstractController{

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

    @Operation(summary = "permet de s'authentifier et de récupérer un token.",
            description = "le token doit être utilisé pour accéder aux ressources protegées.")
    @PostMapping("/connexion")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody ConnexionRequestDto loginRequest) {
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken credential = new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword());

            authentication = authenticationManager.authenticate(credential);
        } catch (AuthenticationException ex) {
            throw new AuthenticationServiceException(Constant.WRONG_LOGIN_AND_OR_PASS);
        }
        if (authentication == null) {
            throw new AuthenticationServiceException(Constant.METHODE_AUTHENTIFICATION_PAS_SUPPORTEE);
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

        return buildResponseEntity(response);

    }

    @Operation(summary = "permet de ",
            description = "le ")
    @PostMapping("/motDePasseOublie")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody MotDePasseOublieRequestDto dto, HttpServletRequest request) throws RestClientException, CaptchaException, JsonIncorrectException {
        String captcha = dto.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException(Constant.EXCEPTION_CAPTCHA_OBLIGATOIRE);
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, ReCaptchaAction.MOT_DE_PASSE_OUBLIE);
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException(Constant.ERROR_RECAPTCHA + reCaptchaResponse.getErrors());
        }

        UserDetailsImpl user;

        if (dto.getEmail() != null) {
            try {
                user = (UserDetailsImpl)userService.loadUser(etablissementService.getUserByMail(dto.getEmail()));

            } catch (IllegalArgumentException ex) {
                throw new UsernameNotFoundException(String.format(Constant.ERROR_UTILISATEUR_NOT_FOUND_MAIL,dto.getEmail()));
            }
        }
        else if (dto.getSiren() != null) {
            try {
                user = (UserDetailsImpl)userService.loadUser(etablissementService.getFirstBySiren(dto.getSiren()));
            } catch (IllegalArgumentException ex) {
                throw new UsernameNotFoundException(String.format(Constant.ERROR_UTILISATEUR_NOT_FOUND_SIREN,dto.getSiren()));
            }
        } else {
            throw new JsonIncorrectException(Constant.CHAMPS_SIREN_OU_EMAIL_OBLIGATOIRE);
        }

        String jwt = tokenProvider.generateToken(user);
        emailService.constructResetTokenEmailUser(jwt, user.getEmail(), user.getNameEtab());

        MotDePasseOublieResponsetDto response = new MotDePasseOublieResponsetDto();
        response.setMessage(Constant.MESSAGE_MDP_OUBLIE);

        return buildResponseEntity(response);
    }

    @Operation(summary = "permet de ",
            description = "le ")
    @PostMapping(value = "/reinitialiserMotDePasse")
    public ResponseEntity<?> resetPasswordConfirm(@Valid @RequestBody ReinitialiserMotDePasseRequestDto request) throws CaptchaException, InvalidTokenException, JsonIncorrectException, MailDoublonException, SirenExistException {
        String captcha = request.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException(Constant.EXCEPTION_CAPTCHA_OBLIGATOIRE);
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, "reinitialisationPass");
         if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException(Constant.ERROR_RECAPTCHA + reCaptchaResponse.getErrors());
        }

        if (request.getTokenFromMail() == null) {
            throw new JsonIncorrectException(Constant.ERROR_AUTHENTIFICATION_TOKEN_OBLIGATOIRE);
        }

        if (request.getMotDePasse() == null) {
            throw new JsonIncorrectException(Constant.ERROR_AUTHENTIFICATION_NOUVEAU_MDP_OBLIGATOIRE);
        }

        if (tokenProvider.validateToken(request.getTokenFromMail())) {
            String siren = tokenProvider.getSirenFromJwtToken(request.getTokenFromMail());
            EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
            ContactEntity contact = etab.getContact();
            contact.setMotDePasse(passwordService.getEncodedMotDePasse(request.getMotDePasse()));
            etablissementService.save(etab);
        } else {
            throw new InvalidTokenException(Constant.ERROR_AUTHENTIFICATION_TOKEN_PAS_VALIDE);
        }

        ReinitialiserMotDePasseResponseDto response = new ReinitialiserMotDePasseResponseDto();
        response.setMessage(Constant.MESSAGE_RESET_MDP);

        return buildResponseEntity(response);
    }

    @PostMapping("/verifierValiditeToken")
    public ResponseEntity<?> verifierValiditeToken(@Valid @RequestBody VerifierValiditeTokenRequestDto request) throws JsonIncorrectException {

        if (request.getToken() == null) {
            throw new JsonIncorrectException(Constant.ERROR_AUTHENTIFICATION_TOKEN_OBLIGATOIRE);
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

        return buildResponseEntity(response);
    }

    @Operation(summary = "permet de mettre à jour le mot de passe une fois connecté")
    @PostMapping(value = "/modifierMotDePasse")
    public ResponseEntity<?> modifierMotDePasse(HttpServletRequest requestHtttp, @Valid @RequestBody ModifierMotDePasseRequestDto request) throws PasswordMismatchException, MailDoublonException, SirenExistException, JsonIncorrectException, InvalidTokenException {

        String siren = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(requestHtttp));

        if (request.getAncienMotDePasse() == null) {
            throw new JsonIncorrectException(Constant.ERROR_AUTHENTIFICATION_ANCIEN_MDP_OBLIGATOIRE);
        }

        if (request.getNouveauMotDePasse() == null) {
            throw new JsonIncorrectException(Constant.ERROR_AUTHENTIFICATION_NOUVEAU_MDP_OBLIGATOIRE);
        }

        String oldPassword = request.getAncienMotDePasse();
        String newPassword = request.getNouveauMotDePasse();

        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        ContactEntity contact = etab.getContact();

        if (passwordService.estLeMotDePasse(oldPassword, contact.getMotDePasse())) {
            if (!passwordService.estLeMotDePasse(newPassword, contact.getMotDePasse())) {
                contact.setMotDePasse(passwordService.getEncodedMotDePasse(newPassword));
                etablissementService.save(etab);
            } else {
                throw new PasswordMismatchException(Constant.ERROR_AUTHENTIFICATION_NOUVEAU_MDP_DIFFERENT_DE_ANCIEN);
            }
        } else {
            throw new PasswordMismatchException(Constant.ERROR_AUTHENTIFICATION_ANCIEN_MDP_DIFFERENT_DE_ACTUEL);
        }

        ModifierMotDePasseResponseDto response = new ModifierMotDePasseResponseDto();
        response.setMessage(Constant.MESSAGE_MDP_MODIFIER);
        return buildResponseEntity(response);
    }
}


