package fr.abes.lnevent.controllers;


import fr.abes.lnevent.dto.etablissement.EtablissementCreeDTO;
import fr.abes.lnevent.recaptcha.ReCaptchaResponse;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.security.jwt.JwtTokenProvider;
import fr.abes.lnevent.security.payload.request.LoginRequest;
import fr.abes.lnevent.security.payload.response.JwtAuthenticationResponse;
import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
import fr.abes.lnevent.services.GenererIdAbes;
import fr.abes.lnevent.services.ReCaptchaCreationCompteService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class AuthenticationController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EtablissementController etablissementController;

    @Autowired
    private GenererIdAbes genererIdAbes;

    @Autowired
    private ReCaptchaCreationCompteService reCaptchaCreationCompteService;



    @ApiOperation(value = "permet de s'authentifier et de récupérer un token.",
            notes = "le token doit être utilisé pour accéder aux ressources protegées.")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("debut authenticateUser");
        log.info("NB : le controller intervient maintenant mais il a au préalable été intercepté par JwtAuthenticationFilter");
        log.info("ce qui permet d'appliquer des filtres en amont du controller comme une ip interdite etc + de vérifier si on a déjà un token");
        log.info("si on a déjà un token, on reste dans le filtre, sinon on est redirigé ici, ce qui explique que dans le filter comme ici, on applique dans tous les cas la méthode authentication");
        log.info("Soit on applique l'authentification dans le controller avec les params login pass (cas connection) ");
        log.info("Soit on applique l'authentification dans le filter avec les params contenus dans le jeton (cas déjà connecté)");
        log.info("LoginRequest login = " + loginRequest.getLogin());
        log.info("LoginRequest password = " + loginRequest.getPassword());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));
        log.info("authenticateUser 1");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //on charge dans user le tuple bdd mis dans authentication
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        log.info("authenticateUser 2");
        String jwt = tokenProvider.generateToken(user);
        log.info("authenticateUser 3");
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, Long.toString(user.getId()), user.getUsername(), user.getNameEtab(), user.getIsAdmin()));
    }


    @PostMapping("/creationCompte")
    public ResponseEntity<?> creationCompte(@Valid @RequestBody EtablissementCreeDTO eventDTO) {
        log.debug("eventDto = " + eventDTO.toString());
        log.debug("NomEtab = " + eventDTO.getNom());
        log.debug("siren = " + eventDTO.getSiren());
        log.debug("TypeEtablissement = " + eventDTO.getTypeEtablissement());
        log.debug("NomContact = " + eventDTO.getNomContact());
        log.debug("PrenomContact = " + eventDTO.getPrenomContact());
        log.debug("AdresseContact = " + eventDTO.getAdresseContact());
        log.debug("BPContact = " + eventDTO.getBoitePostaleContact());
        log.debug("CodePostalContact = " + eventDTO.getCodePostalContact());
        log.debug("VilleContact = " + eventDTO.getVilleContact());
        log.debug("CedexContact = " + eventDTO.getCedexContact());
        log.debug("TelephoneContact = " + eventDTO.getTelephoneContact());
        log.debug("MailContact = " + eventDTO.getMailContact());
        log.debug("mdp = " + eventDTO.getMotDePasse());
        log.debug("recaptcharesponse = " + eventDTO.getRecaptcha());

        String recaptcharesponse = eventDTO.getRecaptcha();

        //verifier la réponse recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaCreationCompteService.verify(recaptcharesponse);
        if(!reCaptchaResponse.isSuccess()){
            return ResponseEntity
                    .badRequest()
                    .body("Erreur ReCaptcha : " +  reCaptchaResponse.getErrors());
        }

        //verifier que le siren n'est pas déjà en base
        boolean existeSiren = etablissementRepository.existeSiren(eventDTO.getSiren());
        log.info("existeSiren = "+ existeSiren);
        if (existeSiren) {
            return ResponseEntity
                    .badRequest()
                    .body("Cet établissement existe déjà.");
        }
        //on crypte le mot de passe + on génère un idAbes + on déclenche la méthode add du controlleur etab
        else{
        log.info("mdp = " + eventDTO.getMotDePasse());
        eventDTO.setMotDePasse(passwordEncoder.encode(eventDTO.getMotDePasse()));
        eventDTO.setIdAbes(genererIdAbes.genererIdAbes(GenererIdAbes.generateId()));
        eventDTO.setRoleContact("etab");
        log.info("idAbes = " + eventDTO.getIdAbes());
        log.info("mdphash = " + eventDTO.getMotDePasse());
        etablissementController.add(eventDTO);
        return ResponseEntity.ok("Creation du compte effectuée.");}
    }
}



