package fr.abes.licencesnationales.web.controllers;

import fr.abes.licencesnationales.services.ArbreService;
import fr.abes.licencesnationales.services.ResetService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Log
@RestController
@RequestMapping("/v1/ln")
public class ServiceController {
    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @GetMapping(value = "/resetEtablissement")
    public void resetEtablissement() {
        resetService.resetEtablissement();
    }

    @GetMapping(value = "/arbre")
    public String arbre() throws ParseException {
        return arbreService.genereArbre();
    }

}
