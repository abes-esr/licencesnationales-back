package fr.abes.lnevent.controllers;

import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.services.ArbreService;
import fr.abes.lnevent.services.ResetService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
public class ServiceController {
    @Autowired
    private EventRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @GetMapping(value = "/ln/resetEtablissement")
    public String resetEtablissement() {
        return resetService.resetEtablissement();
    }

    @GetMapping(value = "/ln/arbre")
    public String arbre() {
        return arbreService.genereArbre();
    }
}
