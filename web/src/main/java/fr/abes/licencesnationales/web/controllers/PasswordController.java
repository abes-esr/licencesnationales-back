package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.PasswordMismatchException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.dto.authentification.PasswordUpdateWebDto;
import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "permet de mettre à jour le mot de passe une fois connecté")
    @PostMapping("/updatePassword")
    public void updatePassword(HttpServletRequest request, @Valid @RequestBody PasswordUpdateWebDto requestData) throws PasswordMismatchException, MailDoublonException, SirenExistException {

        String siren = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(request));
        String oldPassword = requestData.getOldPassword();
        String newPasswordHash = requestData.getNewPassword();

        EtablissementEntity e = etablissementService.getFirstBySiren(siren);
        ContactEntity c = e.getContact();
        //le premier mot de passe ne doit pas être encodé, le second oui
        if (passwordEncoder.matches(oldPassword, c.getMotDePasse())) {
            if (!passwordEncoder.matches(newPasswordHash, c.getMotDePasse())) {
                EtablissementEntity etablissementEntity = etablissementService.getFirstBySiren(siren);
                etablissementEntity.getContact().setMotDePasse(newPasswordHash);
                etablissementService.save(etablissementEntity);
            } else {
                throw new PasswordMismatchException("Votre nouveau mot de passe doit être différent de l'ancien");
            }
        } else {
            throw new PasswordMismatchException("L'ancien mot de passe renseigné ne correspond pas à votre mot de passe actuel.");
        }
    }
}







