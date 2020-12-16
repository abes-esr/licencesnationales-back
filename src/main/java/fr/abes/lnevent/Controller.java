package fr.abes.lnevent;

import fr.abes.lnevent.collection.EventCollection;
import fr.abes.lnevent.collection.repository.EventRepository;
import fr.abes.lnevent.event.EtablissementCreeEvent;
import fr.abes.lnevent.event.EtablissementDiviseEvent;
import fr.abes.lnevent.event.EtablissementFusionneEvent;
import fr.abes.lnevent.event.EtablissementSupprimeEvent;
import fr.abes.lnevent.services.ArbreService;
import fr.abes.lnevent.services.ResetService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@Log
@RestController
public class Controller {

    @Autowired
    private  EventRepository repository;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @PutMapping(value = "/ln/creation/{name}")
    public String add(@PathVariable String name) {
        EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this, name);
        applicationEventPublisher.publishEvent(etablissementCreeEvent);
        repository.save(new EventCollection(etablissementCreeEvent));

        return "done";
    }

    @PostMapping(value = "/ln/fusion/{name}")
    public String fusion(@PathVariable String name, @RequestBody Collection<String> etablissements) {
        EtablissementFusionneEvent etablissementFusionneEvent = new EtablissementFusionneEvent(this, name, new ArrayList<>(etablissements));
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        repository.save(new EventCollection(etablissementFusionneEvent));

        return "done";
    }

    @PostMapping(value = "/ln/division/{name}")
    public String division(@PathVariable String name, @RequestBody Collection<String> etablissements) {
        EtablissementDiviseEvent etablissementDiviseEvent = new EtablissementDiviseEvent(this, name, new ArrayList<>(etablissements));
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        repository.save(new EventCollection(etablissementDiviseEvent));

        return "done";
    }

    @DeleteMapping(value = "/ln/suppression/{name}")
    public String suppression(@PathVariable String name) {
        EtablissementSupprimeEvent etablissementSupprimeEvent = new EtablissementSupprimeEvent(this, name);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        repository.save(new EventCollection(etablissementSupprimeEvent));

        return "done";
    }

    @GetMapping(value = "/ln/resetEtablissement")
    public String resetEtablissement() {
        return resetService.resetEtablissement();
    }

    @GetMapping(value = "/ln/arbre")
    public String arbre() {

        return arbreService.genereArbre();
    }
}
