package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.ip.IpAjouteeDTO;
import fr.abes.lnevent.dto.ip.IpModifieeDTO;
import fr.abes.lnevent.dto.ip.IpSupprimeeDTO;
import fr.abes.lnevent.dto.ip.IpValideeDTO;
import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.entities.EventEntity;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

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
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(value = "/ajout")
    public String ajoutIp(@RequestBody IpAjouteeDTO event) {
        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                event.getIp(),
                event.getSiren(),
                event.getTypeAcces(),
                event.getIp());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        eventRepository.save(new EventEntity(ipAjouteeEvent));

        return "done";
    }

    @PostMapping(value = "/modifie")
    public String edit(@RequestBody IpModifieeDTO ipModifieeDTO) {
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                ipModifieeDTO.getId(),
                ipModifieeDTO.getIp(),
                ipModifieeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        eventRepository.save(new EventEntity(ipModifieeEvent));
        return "done";
    }

    @PostMapping(value = "/valide")
    public String validate(@RequestBody IpValideeDTO ipValideeDTO) {
        IpValideeEvent ipValideeEvent = new IpValideeEvent(this,
                ipValideeDTO.getIp(),
                ipValideeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipValideeEvent);
        eventRepository.save(new EventEntity(ipValideeEvent));
        return "done";
    }

    @PostMapping(value = "/supprime")
    public String delete(@RequestBody IpSupprimeeDTO ipSupprimeeDTO) {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getIp(),
                ipSupprimeeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
        return "done";
    }

    @GetMapping(value = "/{siren}")
    public Set<IpEntity> get(@PathVariable String siren) {
        return etablissementRepository.getFirstBySiren(siren).getIps();
    }









}
