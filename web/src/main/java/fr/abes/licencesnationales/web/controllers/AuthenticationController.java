package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.web.security.jwt.JwtTokenProvider;
import fr.abes.licencesnationales.web.security.payload.request.LoginRequest;
import fr.abes.licencesnationales.web.security.payload.response.JwtAuthenticationResponse;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //on charge dans user le tuple bdd mis dans authentication
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, Long.toString(user.getId()), user.getUsername(), user.getNameEtab(), user.isAdmin()));

    }
}



