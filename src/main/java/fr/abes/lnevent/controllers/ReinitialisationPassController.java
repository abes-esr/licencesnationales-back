package fr.abes.lnevent.controllers;


import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.security.jwt.JwtTokenProvider;
import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
import fr.abes.lnevent.security.services.impl.UserDetailsServiceImpl;
import fr.abes.lnevent.services.EmailService;
import fr.abes.lnevent.services.GenererIdAbes;
import fr.abes.lnevent.services.ReCaptchaCreationCompteService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;



@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/ln/reinitialisationMotDePasse")
public class ReinitialisationPassController {


    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    public JavaMailSender mailSender;

    @Autowired
    EmailService emailService;

    @Autowired
    EtablissementRepository etablissementRepository;

    UserDetails userDetails;



    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/resetPasswordByMail")
    public ResponseEntity<?> resetPasswordByMail(HttpServletRequest request, @Valid @RequestBody String userEmail) throws JSONException {
        log.info("email = " + userEmail);
        JSONObject email = new JSONObject(userEmail);
        String mail = email.getString("email");
        log.info("mail = " + mail);
        EtablissementEntity user = etablissementRepository.getUserByMail(mail);
        if (user==null) {
            return ResponseEntity
                    .badRequest()
                    .body("Le mail renseigné n'a pas été trouvé.");
        }
        userDetails = new UserDetailsServiceImpl().loadUserByEmail(user);


        String jwt = tokenProvider.generateToken((UserDetailsImpl) userDetails);
        String url = emailService.getAppUrl(request);
        mailSender.send(emailService.constructResetTokenEmail(url,
                request.getLocale(), jwt, (UserDetailsImpl)userDetails));

        return ResponseEntity.ok("Nous venons de vous envoyer un mail de réinitialisation de mot de passe.");
    }

    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/resetPasswordBySiren")
    public ResponseEntity<?> resetPasswordBySiren(HttpServletRequest request, @Valid @RequestBody String userSiren) throws JSONException {
        log.info("siren = " + userSiren);
        JSONObject sirenUser = new JSONObject(userSiren);
        String siren = sirenUser.getString("siren");
        log.info("siren = " + siren);
        UserDetails user = new UserDetailsServiceImpl().loadUserByUsername(siren);
        if (user==null) {
            return ResponseEntity
                    .badRequest()
                    .body("Le siren renseigné n'a pas été trouvé.");
        }
        String jwt = tokenProvider.generateToken((UserDetailsImpl) user);
        String url = emailService.getAppUrl(request);
        mailSender.send(emailService.constructResetTokenEmail(url,
                request.getLocale(), jwt, (UserDetailsImpl)user));

        return ResponseEntity.ok("Nous venons de vous envoyer un mail de réinitialisation de mot de passe.");
    }



}



