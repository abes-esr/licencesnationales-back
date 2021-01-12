package fr.abes.lnevent;

import fr.abes.lnevent.entities.EventRow;
import fr.abes.lnevent.repository.*;
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

import java.util.ArrayList;

@Log
@RestController
public class Controller {

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
                = new EtablissementFusionneEvent(this, event.getSiren(), event.getSirenFusionne());
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        repository.save(new EventRow(etablissementFusionneEvent));

        return "done";
    }

    @PostMapping(value = "/ln/division")
    public String division(@RequestBody EtablissementDiviseDTO event) {
        EtablissementDiviseEvent etablissementDiviseEvent
                = new EtablissementDiviseEvent(this, event.getAncienSiren(), new ArrayList<>());
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        repository.save(new EventRow(etablissementDiviseEvent));

        return "done";
    }

    @DeleteMapping(value = "/ln/suppression/{name}")
    public String suppression(@PathVariable String name) {
        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, name);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        repository.save(new EventRow(etablissementSupprimeEvent));

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

    @PostMapping(value = "/ln/ipAjout")
    public String ajoutIp(@RequestBody IpAjouteeDTO event) {
        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                event.getIp(),
                event.getSiren());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        repository.save(new EventRow(ipAjouteeEvent));

        return "done";
    }


}
