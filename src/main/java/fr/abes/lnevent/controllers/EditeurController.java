package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.editeur.EditeurCreeDTO;
import fr.abes.lnevent.dto.editeur.EditeurFusionneDTO;
import fr.abes.lnevent.dto.editeur.EditeurModifieDTO;
import fr.abes.lnevent.event.editeur.EditeurCreeEvent;
import fr.abes.lnevent.event.editeur.EditeurFusionneEvent;
import fr.abes.lnevent.event.editeur.EditeurModifieEvent;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.entities.EventEntity;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("ln/editeur")
public class EditeurController {

    @Autowired
    private EventRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(value = "/creation")
    public String add(@RequestBody EditeurCreeDTO editeurCreeDTO) {
        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this,
                editeurCreeDTO.getNom(),
                editeurCreeDTO.getAdresse(),
                editeurCreeDTO.getMailPourBatch(),
                editeurCreeDTO.getMailPourInformation());
        applicationEventPublisher.publishEvent(editeurCreeEvent);
        repository.save(new EventEntity(editeurCreeEvent));
        return "done";
    }

    @PostMapping(value = "/modification")
    public String edit(@RequestBody EditeurModifieDTO editeurModifieDTO) {
        EditeurModifieEvent editeurModifieEvent = new EditeurModifieEvent(this,
                editeurModifieDTO.getNom(),
                editeurModifieDTO.getAdresse(),
                editeurModifieDTO.getMailPourBatch(),
                editeurModifieDTO.getMailPourInformation());
        applicationEventPublisher.publishEvent(editeurModifieEvent);
        repository.save(new EventEntity(editeurModifieEvent));
        return "done";
    }

    @PostMapping(value = "/fusion")
    public String fusion(@RequestBody EditeurFusionneDTO editeurFusionneDTO) {
        EditeurFusionneEvent editeurFusionneEvent = new EditeurFusionneEvent(this,
                editeurFusionneDTO.getEditeurDTO(),
                editeurFusionneDTO.getIdEditeurFusionnes());
        applicationEventPublisher.publishEvent(editeurFusionneEvent);
        repository.save(new EventEntity(editeurFusionneEvent));
        return "done";
    }
}
