package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.entities.etablissement.PasswordEventEntity;
import fr.abes.licencesnationales.core.repository.PasswordEventRepository;
import fr.abes.licencesnationales.web.dto.password.PasswordEnregistrerWebDto;
import fr.abes.licencesnationales.web.dto.password.PasswordResetWebDto;
import fr.abes.licencesnationales.web.dto.password.PasswordUpdateWebDto;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.event.password.UpdatePasswordEvent;
import fr.abes.licencesnationales.web.dto.password.TokenDto;
import fr.abes.licencesnationales.web.exception.CaptchaException;
import fr.abes.licencesnationales.core.exception.PasswordMismatchException;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
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
    private EmailService emailService;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetails userDetails;

    @Value("${site.url}")
    private String urlSite;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private PasswordEventRepository eventRepository;


    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/resetPassword")
    public void resetPassword(HttpServletRequest request, @Valid @RequestBody PasswordResetWebDto userEmailOrSiren) throws DonneeIncoherenteBddException, RestClientException {
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
        userDetails = new UserDetailsServiceImpl(etablissementService).loadUser(user);

        String jwt = tokenProvider.generateToken((UserDetailsImpl) userDetails);
        String nomEtab = ((UserDetailsImpl) userDetails).getNameEtab();
        String emailUser = ((UserDetailsImpl) userDetails).getEmail();
        emailService.constructResetTokenEmail(urlSite, jwt, emailUser, nomEtab);
    }

    @PostMapping("/verifTokenValide")
    public boolean verifTokenValide(@Valid @RequestBody TokenDto requestData) {
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
    public void enregistrerPassword(@Valid @RequestBody PasswordEnregistrerWebDto requestData) throws RestClientException, CaptchaException {
        String recaptcha = requestData.getRecaptcha();
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
            etablissementService.changePasswordFromSiren(siren, mdp);
        }
    }

    @ApiOperation(value = "permet de mettre à jour le mot de passe une fois connecté")
    @PostMapping("/updatePassword")
    public void updatePassword(HttpServletRequest request, @Valid @RequestBody PasswordUpdateWebDto requestData) throws PasswordMismatchException {

        String siren = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(request));
        String oldPassword = requestData.getOldPassword();
        String newPasswordHash = requestData.getNewPassword();

        EtablissementEntity e = etablissementService.getFirstBySiren(siren);
        ContactEntity c = e.getContact();
        //le premier mot de passe ne doit pas être encodé, le second oui
        if (passwordEncoder.matches(oldPassword, c.getMotDePasse())) {
            if (!passwordEncoder.matches(newPasswordHash, c.getMotDePasse())) {
                UpdatePasswordEvent updatePasswordEvent = new UpdatePasswordEvent(this, siren, newPasswordHash);
                applicationEventPublisher.publishEvent(updatePasswordEvent);
                eventRepository.save(new PasswordEventEntity(updatePasswordEvent));
            } else {
                throw new PasswordMismatchException("Votre nouveau mot de passe doit être différent de l'ancien");
            }
        } else {
            throw new PasswordMismatchException("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel.");
        }
    }
}







