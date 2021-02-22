package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.ip.IpAjouteeDTO;
import fr.abes.lnevent.dto.ip.IpModifieeDTO;
import fr.abes.lnevent.dto.ip.IpSupprimeeDTO;
import fr.abes.lnevent.dto.ip.IpValideeDTO;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.entities.EventEntity;
import fr.abes.lnevent.services.ArbreService;
import fr.abes.lnevent.services.ResetService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("ln/ip")
public class IpController {
    @Autowired
    private EventRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

    @PostMapping(value = "/ajout")
    public String ajoutIp(@RequestBody IpAjouteeDTO event) {
        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                event.getIp(),
                event.getSiren());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        repository.save(new EventEntity(ipAjouteeEvent));

        return "done";
    }

    @PostMapping(value = "/modifie")
    public String edit(@RequestBody IpModifieeDTO ipModifieeDTO) {
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                ipModifieeDTO.getId(),
                ipModifieeDTO.getIp(),
                ipModifieeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        repository.save(new EventEntity(ipModifieeEvent));
        return "done";
    }

    @PostMapping(value = "/valide")
    public String validate(@RequestBody IpValideeDTO ipValideeDTO) {
        IpValideeEvent ipValideeEvent = new IpValideeEvent(this,
                ipValideeDTO.getIp(),
                ipValideeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipValideeEvent);
        repository.save(new EventEntity(ipValideeEvent));
        return "done";
    }

    @PostMapping(value = "/supprime")
    public String delete(@RequestBody IpSupprimeeDTO ipSupprimeeDTO) {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getIp(),
                ipSupprimeeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        repository.save(new EventEntity(ipSupprimeeEvent));
        return "done";
    }


}
