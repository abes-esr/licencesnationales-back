package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.event.password.UpdatePasswordEvent;
import fr.abes.licencesnationales.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.repository.ContactRepository;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.security.exception.DonneeIncoherenteBddException;
import fr.abes.licencesnationales.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.service.EmailService;
import fr.abes.licencesnationales.service.ReCaptchaService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
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
    public JavaMailSender mailSender;

    @Autowired
    EmailService emailService;

    @Autowired
    EtablissementRepository etablissementRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetails userDetails;

    private String emailUser;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EventRepository eventRepository;


    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @Valid @RequestBody String userEmailOrSiren) throws JSONException, DonneeIncoherenteBddException {

        log.info("userEmailOrSiren = " + userEmailOrSiren);
        JSONObject emailOrSiren = new JSONObject(userEmailOrSiren);
        String param = emailOrSiren.keys().next().toString();
        log.info("param = " + param);
        String mail = null;
        String siren = null;
        EtablissementEntity user = null;
        String msgErr = "Identifiant non connu dans la base ; merci de contacter l’assistance https://stp.abes.fr/node/3?origine=LicencesNationales";
        if(param.equals("email")) {
            mail = emailOrSiren.getString("email");
            log.info("mail = " + mail);
            user = etablissementRepository.getUserByMail(mail);
        }
        else{
            siren = emailOrSiren.getString("siren");
            user = etablissementRepository.getFirstBySiren(siren);
        }
        if (param.equals("email") && user==null) {
            return ResponseEntity
                    .badRequest()
                    .body(msgErr);
        }else if(param.equals("siren") && user==null){
            return ResponseEntity
                    .badRequest()
                    .body(msgErr);
        }
        userDetails = new UserDetailsServiceImpl().loadUser(user);

        String jwt = tokenProvider.generateToken((UserDetailsImpl) userDetails);
        String nomEtab = ((UserDetailsImpl) userDetails).getNameEtab();
        String url = emailService.getAppUrl(request);
        emailUser = ((UserDetailsImpl) userDetails).getEmail();
        emailService.constructResetTokenEmail(url,
                request.getLocale(), jwt, emailUser, nomEtab);

        return ResponseEntity.ok("Nous venons de vous envoyer un mail de réinitialisation de mot de passe.");
    }

    @PostMapping("/verifTokenValide")
    public ResponseEntity<?> verifTokenValide(HttpServletRequest request, @Valid @RequestBody String requestData) throws JSONException {
        log.info("requestDataVerifTokenValid = " + requestData);
        JSONObject data = new JSONObject(requestData);
        String jwtToken = data.getString("jwtToken");
        log.info("token = " + jwtToken);
        if(!tokenProvider.validateToken(jwtToken)){
            return ResponseEntity
                    .badRequest()
                    .body("Token invalide" );
        }
        else{
            return ResponseEntity.ok("Token valide");
        }
    }


    @ApiOperation(value = "permet de ",
            notes = "le ")
    @PostMapping("/enregistrerPassword")
    public ResponseEntity<?> enregistrerPassword(HttpServletRequest request, @Valid @RequestBody String requestData) throws JSONException {
        log.info("requestData = " + requestData);
        JSONObject data = new JSONObject(requestData);
        String recaptcha = data.getString("fr/abes/licencesnationales/recaptcha");
        log.info("fr.abes.licencesnationales.recaptcha = " + recaptcha);
        String mdp = data.getString("motDePasse");
        log.info("mdp = " + mdp);
        String token = data.getString("token");
        log.info("token = " + token);
        String action = "reinitialisationPass";

        //verifier la réponse fr.abes.licencesnationales.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(recaptcha, action);
        if(!reCaptchaResponse.isSuccess()){
            return ResponseEntity
                    .badRequest()
                    .body("Erreur ReCaptcha : " +  reCaptchaResponse.getErrors());
        }
        if(!tokenProvider.validateToken(token)){
            return ResponseEntity
                    .badRequest()
                    .body("Token invalide" );
        }
        String siren = tokenProvider.getSirenFromJwtToken(token);
        log.info("siren = " + siren);
        String mdphash = passwordEncoder.encode(mdp);
        log.info("mdphash = " + mdp);
        EtablissementEntity e = etablissementRepository.getFirstBySiren(siren);
        ContactEntity c = e.getContact();
        c.setMotDePasse(mdphash);
        contactRepository.save(c);
        emailUser = c.getMail();
        emailService.constructValidationNewPassEmail( request.getLocale(), emailUser);
        return ResponseEntity.ok("Votre mot de passe a bien été réinitialisé. Nous venons de vous envoyer un mail de confirmation de réinitialisation de mot de passe.");
    }

    @ApiOperation(value = "permet de mettre à jour le mot de passe une fois connecté")
    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(HttpServletRequest request, @Valid @RequestBody String requestData) throws JSONException {
        JSONObject data = new JSONObject(requestData);
        String siren = tokenProvider.getSirenFromJwtToken(tokenProvider.getJwtFromRequest(request));
        String oldPassword = data.getString("oldPassword");
        String newPasswordHash = passwordEncoder.encode(data.getString("newPassword"));

        EtablissementEntity e = etablissementRepository.getFirstBySiren(siren);
        ContactEntity c = e.getContact();

        if (passwordEncoder.matches(oldPassword, c.getMotDePasse())) {
            UpdatePasswordEvent updatePasswordEvent = new UpdatePasswordEvent(this, siren, newPasswordHash);
            applicationEventPublisher.publishEvent(updatePasswordEvent);
            eventRepository.save(new EventEntity(updatePasswordEvent));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le mot de passe renseigné ne correspond pas à votre ancien mot de passe.");
        }

        return ResponseEntity.ok("Mot de passe mis à jour.");
    }
}







