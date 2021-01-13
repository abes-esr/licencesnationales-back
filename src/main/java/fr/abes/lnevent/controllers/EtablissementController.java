package fr.abes.lnevent.controllers;

import fr.abes.lnevent.repository.entities.EventRow;
import fr.abes.lnevent.event.*;
import fr.abes.lnevent.dto.EtablissementCreeDTO;
import fr.abes.lnevent.dto.EtablissementDiviseDTO;
import fr.abes.lnevent.dto.EtablissementFusionneDTO;
import fr.abes.lnevent.dto.IpAjouteeDTO;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.services.ArbreService;
import fr.abes.lnevent.services.ResetService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
public class EtablissementController {

    @Autowired
    private EventRepository repository;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @PostMapping(value = "/ln/creation")
    public String add(@RequestBody EtablissementCreeDTO event) {
        EtablissementCreeEvent etablissementCreeEvent =
                new EtablissementCreeEvent(this,
                        event.getNom(),
                        event.getAdresse(),
                        event.getSiren(),
                        event.getTypeEtablissement(),
                        event.getMotDePasse(),
                        event.getIdAbes(),
                        event.getMailContact(),
                        event.getNomContact(),
                        event.getPrenomContact(),
                        event.getTelephoneContact(),
                        event.getAdresseContact());
        applicationEventPublisher.publishEvent(etablissementCreeEvent);
        repository.save(new EventRow(etablissementCreeEvent));

        return "done";
    }

    @PostMapping(value = "/ln/fusion")
    public String fusion(@RequestBody EtablissementFusionneDTO event) {
        EtablissementFusionneEvent etablissementFusionneEvent
                = new EtablissementFusionneEvent(this, event.getEtablissement(), event.getSirenFusionne());
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        repository.save(new EventRow(etablissementFusionneEvent));

        return "done";
    }

    @PostMapping(value = "/ln/division")
    public String division(@RequestBody EtablissementDiviseDTO event) {
        EtablissementDiviseEvent etablissementDiviseEvent
                = new EtablissementDiviseEvent(this, event.getAncienSiren(), event.getEtablissements());
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        repository.save(new EventRow(etablissementDiviseEvent));

        return "done";
    }

    @DeleteMapping(value = "/ln/suppression/{siren}")
    public String suppression(@PathVariable String siren) {
        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        repository.save(new EventRow(etablissementSupprimeEvent));

        return "done";
    }






}
