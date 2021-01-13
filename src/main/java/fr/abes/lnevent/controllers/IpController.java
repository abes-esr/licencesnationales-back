package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.IpAjouteeDTO;
import fr.abes.lnevent.event.IpAjouteeEvent;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.repository.entities.EventRow;
import fr.abes.lnevent.services.ArbreService;
import fr.abes.lnevent.services.ResetService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
public class IpController {
    @Autowired
    private EventRepository repository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ResetService resetService;

    @Autowired
    private ArbreService arbreService;

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
