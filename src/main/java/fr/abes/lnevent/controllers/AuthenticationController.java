package fr.abes.lnevent.controllers;


import fr.abes.lnevent.dto.User;
import fr.abes.lnevent.dto.etablissement.EtablissementCreeDTO;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.security.jwt.JwtTokenProvider;
import fr.abes.lnevent.security.payload.request.LoginRequest;
import fr.abes.lnevent.security.payload.response.JwtAuthenticationResponse;
import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
import fr.abes.lnevent.services.GenererIdAbes;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class AuthenticationController {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EtablissementController etablissementController;

    @Autowired
    GenererIdAbes genererIdAbes;





   /* @PostMapping("/register")
    @ApiOperation(
            value = "Enregistrer un nouvel établissement et son contact",
            notes = "Enregistre un nouvel établissement et son contact")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération terminée avec succès."),
            @ApiResponse(code = 503, message = "Service indisponible."),
            @ApiResponse(code = 400, message = "Mauvaise requête. Le paramètre problématique sera précisé par le message d'erreur. Par exemple : paramètre manquant, adresse erronnée..."),
            @ApiResponse(code = 404, message = "Opération a échoué."),
    })
    public AppUser register(
            @ApiParam(value = "Objet JSON contenant les informations sur l'utilisateur à enregistrer. Tous les champs sont nécessairese.", required = true)
            @PathParam("user")
            @Valid @NotNull @RequestBody EtablissementCreeDTO eventDTO) {

        final String uri = "http://localhost:8080/creation?iddoc=" + this.iddoc + "&contexte=" + this.contexte;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        return userService.createUser(user);
    }
*/

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
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //on charge dans user le tuple bdd mis dans authentication
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        log.info("là");
        String jwt = tokenProvider.generateToken(user);
        log.info("ici");
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, Long.toString(user.getId()), user.getUsername(), user.getAuthorities().iterator().next().toString().equals("admin") ? "true" : "false"));
    }


    @PostMapping("/creationCompte")
    public ResponseEntity<?> creationCompte(@Valid @RequestBody EtablissementCreeDTO eventDTO) {
        log.info("eventDto = " + eventDTO.toString());
        log.info("siren = " + eventDTO.getSiren());
        log.info("mdp = " + eventDTO.getMotDePasse());
        log.info("mdp = " + eventDTO.getNomContact());
        log.info("mdp = " + eventDTO.getNom());
        log.info("mdp = " + eventDTO.getAdresseContact());
        log.info("mdp = " + eventDTO.getCedexContact());
        log.info("mdp = " + eventDTO.getCodePostalContact());
        log.info("mdp = " + eventDTO.getPrenomContact());
        log.info("mdp = " + eventDTO.getTelephoneContact());
        log.info("mdp = " + eventDTO.getTypeEtablissement());
        boolean existeSiren = contactRepository.existeSiren(eventDTO.getSiren());
        log.info("existeSiren = "+ existeSiren);
        if (existeSiren) {
            return ResponseEntity
                    .badRequest()
                    .body("Cet établissement existe déjà.");
        }
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



