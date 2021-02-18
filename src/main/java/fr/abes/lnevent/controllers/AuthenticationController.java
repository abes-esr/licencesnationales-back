package fr.abes.lnevent.controllers;


import fr.abes.lnevent.dto.User;
import fr.abes.lnevent.entities.ContactRow;
import fr.abes.lnevent.entities.EtablissementRow;
import fr.abes.lnevent.security.jwt.JwtTokenProvider;
import fr.abes.lnevent.security.payload.request.LoginRequest;
import fr.abes.lnevent.security.payload.response.JwtAuthenticationResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


/**
 * Controlleur d'API RESTful pour toutes les routes publiques.
 * Cette classe est basée sur le framework Spring avec le module Spring Web.
 * @since 0.0.1
 * @author Duy Tran
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class AuthenticationController {


    //private final IUserService userService;


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;



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
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //User user = (User)authentication.getPrincipal();
        ContactRow user = (ContactRow)authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(user);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, user.getId(), user.getSiren(), user.getIsAdmin()));
    }
}


}
