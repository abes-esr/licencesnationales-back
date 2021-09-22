package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.dto.authentification.ConnexionRequestDto;
import fr.abes.licencesnationales.web.dto.authentification.ConnexionResponseDto;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v1/authentification")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "permet de s'authentifier et de récupérer un token.",
            notes = "le token doit être utilisé pour accéder aux ressources protegées.")
    @PostMapping("/connexion")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody ConnexionRequestDto loginRequest) {
        UsernamePasswordAuthenticationToken credential = new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(credential);

        if (authentication == null) {
            throw new AuthenticationServiceException("La méthode d'authentification n'est pas supportée");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //on charge dans user le tuple bdd mis dans authentication
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal(); //ici si mauvais credentials NestedServletException
        String jwt = tokenProvider.generateToken(user);

        ConnexionResponseDto response = new ConnexionResponseDto();
        response.setAccessToken(jwt);
        response.setUserId(user.getId());
        response.setUserNameEtab(user.getNameEtab());
        response.setUserSiren(user.getSiren());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);

    }
}


