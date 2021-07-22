package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.security.payload.request.LoginRequest;
import fr.abes.licencesnationales.security.payload.response.JwtAuthenticationResponse;
import fr.abes.licencesnationales.security.services.impl.UserDetailsImpl;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1")
public class AuthenticationController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;


    @ApiOperation(value = "permet de s'authentifier et de récupérer un token.",
            notes = "le token doit être utilisé pour accéder aux ressources protegées.")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("debut authenticateUser");
        log.info("NB : le controller intervient maintenant mais il a au préalable été intercepté par JwtAuthenticationFilter");
        log.info("ce qui permet d'appliquer des filtres en amont du controller comme une ip interdite etc + de vérifier si on a déjà un token");
        log.info("si on a déjà un token, on reste dans le filtre, sinon on est redirigé ici, ce qui explique que dans le filter comme ici, on applique dans tous les cas la méthode authentication");
        log.info("Soit on applique l'authentification dans le controller avec les params login pass (cas connection) ");
        log.info("Soit on applique l'authentification dans le filter avec les params contenus dans le jeton (cas déjà connecté)");
        log.info("LoginRequest login = " + loginRequest.getLogin());
        log.info("LoginRequest password = " + loginRequest.getPassword());
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));
            log.info("authenticateUser 1");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            try{
                //on charge dans user le tuple bdd mis dans authentication
                UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
                log.info("authenticateUser 2");
                String jwt = tokenProvider.generateToken(user);
                log.info("authenticateUser 3");
                return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, Long.toString(user.getId()), user.getUsername(), user.getNameEtab(), user.getIsAdmin()));
            }
            catch (UsernameNotFoundException e) {
                return ResponseEntity
                        .badRequest()
                        .body("La combinaison siren/mot de passe n'a pas été trouvée.");
            }
        }
        catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("La combinaison siren/mot de passe n'a pas été trouvée.");
        }
    }
}



