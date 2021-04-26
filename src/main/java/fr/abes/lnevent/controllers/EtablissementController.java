package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.etablissement.*;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.etablissement.*;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.exception.AccesInterditException;
import fr.abes.lnevent.exception.SirenIntrouvableException;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.security.services.impl.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;



@Slf4j
@RestController
@RequestMapping("ln/etablissement")
public class EtablissementController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;



    //@PostMapping(value = "/creation")
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
        EtablissementModifieEvent etablissementModifieEvent =
                new EtablissementModifieEvent(this,
                        getSirenFromSecurityContextUser(),
                        eventDTO.getNomContact(),
                        eventDTO.getPrenomContact(),
                        eventDTO.getMailContact(),
                        eventDTO.getTelephoneContact(),
                        eventDTO.getAdresseContact(),
                        eventDTO.getBoitePostaleContact(),
                        eventDTO.getCodePostalContact(),
                        eventDTO.getVilleContact(),
                        eventDTO.getCedexContact());
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
    @PreAuthorize("hasAuthority('admin')")
    public String suppression(@PathVariable String siren) {
        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        eventRepository.save(new EventEntity(etablissementSupprimeEvent));

        return "done";
    }

    @GetMapping(value = "/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public EtablissementEntity get(@PathVariable String siren) {
        return etablissementRepository.getFirstBySiren(siren);
    }

    @GetMapping(value = "/getInfoEtab")
    public EtablissementEntity getInfoEtab() throws SirenIntrouvableException, AccesInterditException {
        return etablissementRepository.getFirstBySiren(getSirenFromSecurityContextUser());
    }

    private String getSirenFromSecurityContextUser() throws SirenIntrouvableException, AccesInterditException{
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sirenFromSecurityContextUser = userDetails.getUsername();
        log.info("sirenFromSecurityContextUser = " + sirenFromSecurityContextUser);
        if(sirenFromSecurityContextUser.equals("") || sirenFromSecurityContextUser==null){
            log.error("Acces interdit");
            throw new AccesInterditException("Acces interdit");
        }
        boolean existeSiren = etablissementRepository.existeSiren(sirenFromSecurityContextUser);
        log.info("existeSiren = "+ existeSiren);
        if (!existeSiren) {
            log.error("Siren absent de la base");
            throw new SirenIntrouvableException("Siren absent de la base");
        }
        return sirenFromSecurityContextUser;
    }
}
