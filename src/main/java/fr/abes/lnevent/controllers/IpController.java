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
import fr.abes.lnevent.services.EmailService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    @Autowired
    public JavaMailSender mailSender;

    @Autowired
    EmailService emailService;

    @Value("${ln.dest.notif.admin}")
    private String admin;



    @PostMapping(value = "/ajout")
    public ResponseEntity<?> ajoutIp(@RequestBody IpAjouteeDTO event) throws SirenIntrouvableException, AccesInterditException {
        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                event.getIp(),
                filtrerAccesServices.getSirenFromSecurityContextUser(),
                event.getTypeAcces(),
                event.getIp(),
                event.getCommentaires());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        eventRepository.save(new EventEntity(ipAjouteeEvent));

        String etab = etablissementRepository.getFirstBySiren(filtrerAccesServices.getSirenFromSecurityContextUser()).getName();
        String descriptionAcces = "id = " + event.getId() + ", ip = " + event.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        mailSender.send(emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, event.getCommentaires(), admin ));

        return ResponseEntity.ok("L'accès a bien été créé.");
    }


    @PostMapping(value = "/modification")
    public ResponseEntity<?> edit(@RequestBody IpModifieeDTO ipModifieeDTO) throws SirenIntrouvableException, AccesInterditException {
        log.info("debut IpController modification");
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                filtrerAccesServices.getSirenFromSecurityContextUser(),
                ipModifieeDTO.getId(),
                ipModifieeDTO.getIp(),
                ipModifieeDTO.getValidee(),
                //ipModifieeDTO.getDateModification() pour le moment ce champ n'est pas necessaire puisqu'on fixe la date de modif dans le listener
                ipModifieeDTO.getTypeAcces(),
                ipModifieeDTO.getTypeIp(),
                ipModifieeDTO.getCommentaires());
        log.info("IpController modification2");
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        log.info("IpController modification3");
        eventRepository.save(new EventEntity(ipModifieeEvent));
        String etab = etablissementRepository.getFirstBySiren(filtrerAccesServices.getSirenFromSecurityContextUser()).getName();
        String descriptionAcces = "id = " + ipModifieeDTO.getId() + ", ip = " + ipModifieeDTO.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        mailSender.send(emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, ipModifieeDTO.getCommentaires(), admin ));

        return ResponseEntity.ok("L'accès a bien été modifié.");
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
