package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.ip.*;
import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.exception.AccesInterditException;
import fr.abes.lnevent.exception.SirenIntrouvableException;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.security.services.FiltrerAccesServices;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Log
@RestController
@RequestMapping("ln/ip")
public class IpController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    FiltrerAccesServices filtrerAccesServices;

    @PostMapping(value = "/ajout")
    public String ajoutIp(@RequestBody IpAjouteeDTO event) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(event.getSiren());
        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                event.getIp(),
                event.getSiren(),
                event.getTypeAcces(),
                event.getIp());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        eventRepository.save(new EventEntity(ipAjouteeEvent));

        return "done";
    }

    @PostMapping(value = "/modification")
    public String edit(@RequestBody IpModifieeDTO ipModifieeDTO) throws SirenIntrouvableException, AccesInterditException {
        log.info("debut IpController modification");
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                ipModifieeDTO.getSiren(),
                ipModifieeDTO.getId(),
                ipModifieeDTO.getIp(),
                ipModifieeDTO.getValidee(),
                ipModifieeDTO.getDateModification(),
                ipModifieeDTO.getTypeAcces(),
                ipModifieeDTO.getTypeIp());
                //ipModifieeDTO.getSirenFromSecurityContextUser());
        log.info("IpController modification2");
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        log.info("IpController modification3");
        eventRepository.save(new EventEntity(ipModifieeEvent));
        return "done";
    }

    @PostMapping(value = "/valide")
    public String validate(@RequestBody IpValideeDTO ipValideeDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipValideeDTO.getSiren());
        IpValideeEvent ipValideeEvent = new IpValideeEvent(this,
                ipValideeDTO.getIp(),
                ipValideeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipValideeEvent);
        eventRepository.save(new EventEntity(ipValideeEvent));
        return "done";
    }

    @PostMapping(value = "/supprime")
    public String delete(@RequestBody IpSupprimeeDTO ipSupprimeeDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipSupprimeeDTO.getSiren());
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getIp(),
                ipSupprimeeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
        return "done";
    }

    @GetMapping(value = "/{siren}")
    public Set<IpEntity> get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        return etablissementRepository.getFirstBySiren(siren).getIps();
    }

    @PostMapping(value = "/getIpEntity")
    public IpEntity getIpEntity(@RequestBody IpDTO ipDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipDTO.getSiren());
        Long identifiant = Long.parseLong(ipDTO.getId());
        return ipRepository.getFirstById(identifiant);
    }









}
