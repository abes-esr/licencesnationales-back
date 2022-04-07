package fr.abes.licencesnationales.web.controllers;

import fr.abes.licencesnationales.core.services.ArbreService;
import fr.abes.licencesnationales.core.services.ResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/v1")
public class ServiceController {
    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @GetMapping(value = "/resetEtablissement")
    public void resetEtablissement() throws IOException {
        resetService.resetEtablissement();
    }

    @GetMapping(value = "/arbre")
    public String arbre() throws ParseException, IOException {
        return arbreService.genereArbre();
    }

    @GetMapping(value = "/applicationVersion")
    public String version() {
        return this.getClass().getPackage().getImplementationVersion();
    }

}
