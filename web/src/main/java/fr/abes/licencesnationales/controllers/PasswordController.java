package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.dto.PasswordWebDto;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.event.password.UpdatePasswordEvent;
import fr.abes.licencesnationales.exception.CaptchaException;
import fr.abes.licencesnationales.exception.PasswordMismatchException;
import fr.abes.licencesnationales.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.service.ReCaptchaService;
import fr.abes.licencesnationales.services.ContactService;
import fr.abes.licencesnationales.services.EmailService;
import fr.abes.licencesnationales.services.EtablissementService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1/ln/reinitialisationMotDePasse")
public class PasswordController {


    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    EmailService emailService;

    @Autowired
    EtablissementService etablissementService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetails userDetails;

    private String emailUser;

    @Value("${site.url}")
    private String urlSite;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EventRepository eventRepository;


    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/resetPassword")
    public void resetPassword(HttpServletRequest request, @Valid @RequestBody PasswordWebDto userEmailOrSiren) throws DonneeIncoherenteBddException, RestClientException {
        String mail;
        String siren;
        EtablissementEntity user;
        String msgErr = "Identifiant non connu dans la base ; merci de contacter l’assistance https://stp.abes.fr/node/3?origine=LicencesNationales";
        if (userEmailOrSiren.getEmail() != null) {
            mail = userEmailOrSiren.getEmail();
            log.info("mail = " + mail);
            user = etablissementService.getUserByMail(mail);
        } else {
            siren = userEmailOrSiren.getSiren();
            user = etablissementService.getFirstBySiren(siren);
        }
        if (userEmailOrSiren.getEmail() != null && user == null) {
            throw new AuthenticationCredentialsNotFoundException(msgErr);
        } else
            if (userEmailOrSiren.getSiren() != null && user == null) {
                throw new AuthenticationCredentialsNotFoundException(msgErr);
        }
        userDetails = new UserDetailsServiceImpl().loadUser(user);

        String jwt = tokenProvider.generateToken((UserDetailsImpl) userDetails);
        String nomEtab = ((UserDetailsImpl) userDetails).getNameEtab();
        emailUser = ((UserDetailsImpl) userDetails).getEmail();
        emailService.constructResetTokenEmail(urlSite, request.getLocale(), jwt, emailUser, nomEtab);
    }

    @PostMapping("/verifTokenValide")
    public boolean verifTokenValide(@Valid @RequestBody PasswordWebDto requestData) {
        log.info("requestDataVerifTokenValid = " + requestData);
        String jwtToken = requestData.getToken();
        log.info("token = " + jwtToken);
        if (!tokenProvider.validateToken(jwtToken)) {
            return false;
        } else {
            return true;
        }
    }


    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/enregistrerPassword")
    public void enregistrerPassword(HttpServletRequest request, @Valid @RequestBody PasswordWebDto requestData) throws RestClientException, CaptchaException {
        String recaptcha = requestData.getRepatcha();
        String mdp = requestData.getPassword();
        String token = requestData.getToken();
        String action = "reinitialisationPass";

        //verifier la réponse fr.abes.licencesnationales.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(recaptcha, action);
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException(StringUtils.joinWith("\n", reCaptchaResponse.getErrors()));
        }
        if (tokenProvider.validateToken(token)) {
            String siren = tokenProvider.getSirenFromJwtToken(token);
            log.info("siren = " + siren);
            String mdphash = passwordEncoder.encode(mdp);
            log.info("mdphash = " + mdp);
            EtablissementEntity e = etablissementService.getFirstBySiren(siren);
            ContactEntity c = e.getContact();
            c.setMotDePasse(mdphash);
            contactService.save(c);
            emailUser = c.getMail();
            emailService.constructValidationNewPassEmail(request.getLocale(), emailUser);
        }
    }

    @ApiOperation(value = "permet de mettre à jour le mot de passe une fois connecté")
    @PostMapping("/updatePassword")
    public void updatePassword(HttpServletRequest request, @Valid @RequestBody PasswordWebDto requestData) throws PasswordMismatchException {

        String siren = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(request));
        String oldPassword = requestData.getOldPassword();
        String newPasswordHash = passwordEncoder.encode(requestData.getNewPassword());

        EtablissementEntity e = etablissementService.getFirstBySiren(siren);
        ContactEntity c = e.getContact();

        if (passwordEncoder.matches(oldPassword, c.getMotDePasse())) {
            UpdatePasswordEvent updatePasswordEvent = new UpdatePasswordEvent(this, siren, newPasswordHash);
            applicationEventPublisher.publishEvent(updatePasswordEvent);
            eventRepository.save(new EventEntity(updatePasswordEvent));
        } else {
            throw new PasswordMismatchException("Le mot de passe renseigné ne correspond pas à votre ancien mot de passe.");
        }
    }
}







