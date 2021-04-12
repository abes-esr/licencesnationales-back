package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.etablissement.*;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.etablissement.*;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.exception.AccesInterditException;
import fr.abes.lnevent.exception.SirenIntrouvableException;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.security.services.FiltrerAccesServices;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log
@RestController
@RequestMapping("ln/etablissement")
public class EtablissementController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    FiltrerAccesServices filtrerAccesServices;


    @PostMapping(value = "/creation")
    public String add(@RequestBody EtablissementCreeDTO eventDTO) {
        EtablissementCreeEvent etablissementCreeEvent =
                new EtablissementCreeEvent(this,
                        eventDTO);
        applicationEventPublisher.publishEvent(etablissementCreeEvent);
        eventRepository.save(new EventEntity(etablissementCreeEvent));

        return "done";
    }

    @PostMapping(value = "/modification")
    public String edit(@Valid @RequestBody EtablissementModifieDTO eventDTO) throws SirenIntrouvableException, AccesInterditException{
        log.info("debut EtablissementController modification");
        filtrerAccesServices.autoriserServicesParSiren(eventDTO.getSiren());
        EtablissementModifieEvent etablissementModifieEvent =
                new EtablissementModifieEvent(this,
                        eventDTO.getSiren(),
                        eventDTO.getNomContact(),
                        eventDTO.getAdresseContact(),
                        eventDTO.getMailContact(),
                        eventDTO.getTelephoneContact());
        applicationEventPublisher.publishEvent(etablissementModifieEvent);
        eventRepository.save(new EventEntity(etablissementModifieEvent));

        return "done";
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public String fusion(@RequestBody EtablissementFusionneDTO eventDTO) {
        EtablissementFusionneEvent etablissementFusionneEvent
                = new EtablissementFusionneEvent(this, eventDTO.getEtablissementDTO(), eventDTO.getSirenFusionnes());
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        eventRepository.save(new EventEntity(etablissementFusionneEvent));

        return "done";
    }

    @PostMapping(value = "/division")
    @PreAuthorize("hasAuthority('admin')")
    public String division(@RequestBody EtablissementDiviseDTO eventDTO) {
        EtablissementDiviseEvent etablissementDiviseEvent
                = new EtablissementDiviseEvent(this, eventDTO.getAncienSiren(), eventDTO.getEtablissementDTOS());
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        eventRepository.save(new EventEntity(etablissementDiviseEvent));

        return "done";
    }

    @DeleteMapping(value = "/suppression/{siren}")
    public String suppression(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        eventRepository.save(new EventEntity(etablissementSupprimeEvent));

        return "done";
    }

    @GetMapping(value = "/{siren}")
    public EtablissementEntity get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        return etablissementRepository.getFirstBySiren(siren);
    }






}
