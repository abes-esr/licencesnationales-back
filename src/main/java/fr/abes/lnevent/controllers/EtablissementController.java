package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.etablissement.EtablissementCreeDTO;
import fr.abes.lnevent.dto.etablissement.EtablissementDiviseDTO;
import fr.abes.lnevent.dto.etablissement.EtablissementFusionneDTO;
import fr.abes.lnevent.dto.etablissement.EtablissementModifieDTO;
import fr.abes.lnevent.event.etablissement.*;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.repository.EventRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("ln/Etablissement")
public class EtablissementController {

    @Autowired
    private EventRepository repository;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(value = "/creation")
    public String add(@RequestBody EtablissementCreeDTO eventDTO) {
        EtablissementCreeEvent etablissementCreeEvent =
                new EtablissementCreeEvent(this,
                        eventDTO.getNom(),
                        eventDTO.getAdresse(),
                        eventDTO.getSiren(),
                        eventDTO.getTypeEtablissement(),
                        eventDTO.getMotDePasse(),
                        eventDTO.getIdAbes(),
                        eventDTO.getMailContact(),
                        eventDTO.getNomContact(),
                        eventDTO.getPrenomContact(),
                        eventDTO.getTelephoneContact(),
                        eventDTO.getAdresseContact());
        applicationEventPublisher.publishEvent(etablissementCreeEvent);
        repository.save(new EventEntity(etablissementCreeEvent));

        return "done";
    }

    @PostMapping(value = "/modification")
    public String edit(@RequestBody EtablissementModifieDTO eventDTO) {
        EtablissementModifieEvent etablissementModifieEvent =
                new EtablissementModifieEvent(this,
                        eventDTO.getId(),
                        eventDTO.getNom(),
                        eventDTO.getAdresse(),
                        eventDTO.getSiren(),
                        eventDTO.getTypeEtablissement(),
                        eventDTO.getMotDePasse(),
                        eventDTO.getIdAbes(),
                        eventDTO.getIdContact(),
                        eventDTO.getMailContact(),
                        eventDTO.getNomContact(),
                        eventDTO.getPrenomContact(),
                        eventDTO.getTelephoneContact(),
                        eventDTO.getAdresseContact());
        applicationEventPublisher.publishEvent(etablissementModifieEvent);
        repository.save(new EventEntity(etablissementModifieEvent));

        return "done";
    }

    @PostMapping(value = "/fusion")
    public String fusion(@RequestBody EtablissementFusionneDTO eventDTO) {
        EtablissementFusionneEvent etablissementFusionneEvent
                = new EtablissementFusionneEvent(this, eventDTO.getEtablissementDTO(), eventDTO.getSirenFusionnes());
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        repository.save(new EventEntity(etablissementFusionneEvent));

        return "done";
    }

    @PostMapping(value = "/division")
    public String division(@RequestBody EtablissementDiviseDTO eventDTO) {
        EtablissementDiviseEvent etablissementDiviseEvent
                = new EtablissementDiviseEvent(this, eventDTO.getAncienSiren(), eventDTO.getEtablissementDTOS());
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        repository.save(new EventEntity(etablissementDiviseEvent));

        return "done";
    }

    @DeleteMapping(value = "/suppression/{siren}")
    public String suppression(@PathVariable String siren) {
        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        repository.save(new EventEntity(etablissementSupprimeEvent));

        return "done";
    }






}
