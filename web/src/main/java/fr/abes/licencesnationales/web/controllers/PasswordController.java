package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.web.dto.PasswordWebDto;
import fr.abes.licencesnationales.core.entities.ContactEntity;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.event.password.UpdatePasswordEvent;
import fr.abes.licencesnationales.web.exception.CaptchaException;
import fr.abes.licencesnationales.core.exception.PasswordMismatchException;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import fr.abes.licencesnationales.core.services.ContactService;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
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







