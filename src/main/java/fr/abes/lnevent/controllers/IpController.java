package fr.abes.lnevent.controllers;

import fr.abes.lnevent.constant.Constant;
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
import fr.abes.lnevent.services.IpService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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

    @Autowired
    IpService ipService;

    @Value("${ln.dest.notif.admin}")
    private String admin;


    @PostMapping(value = "/ajoutIpV4")
    public ResponseEntity<?> ajoutIpv4(@Valid @RequestBody Ipv4AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterAjoutIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutIpV6")
    public ResponseEntity<?> ajoutIpv6(@Valid @RequestBody Ipv6AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterAjoutIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV4")
    public ResponseEntity<?> ajoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterAjoutIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV6")
    public ResponseEntity<?> ajoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterAjoutIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    public ResponseEntity<?> traiterAjoutIp(IpAjouteeDTO event, String sirenFromSecurityContext){
        log.info("ipService.checkDoublonIpAjouteeDto(event).getStatusCode() ="+ ipService.checkDoublonIpAjouteeDto(event).getStatusCode());
        log.info("String value of =  "+ String.valueOf(ipService.checkDoublonIpAjouteeDto(event).getStatusCodeValue()));
        ResponseEntity doublonIp = ipService.checkDoublonIpAjouteeDto(event);
        if(doublonIp.getStatusCodeValue()==400){
            return doublonIp;
        }
        else {
            IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                    sirenFromSecurityContext,
                    event.getTypeIp(),
                    event.getTypeAcces(),
                    event.getIp());
            applicationEventPublisher.publishEvent(ipAjouteeEvent);
            eventRepository.save(new EventEntity(ipAjouteeEvent));

            String etab = etablissementRepository.getFirstBySiren(sirenFromSecurityContext).getName();
            String descriptionAcces = " ip ou plage d'ips = " + event.getIp() + " en provenance de l'établissement " + etab;
            log.info("admin = " + admin);
            mailSender.send(emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, event.getCommentaires(), admin));

            return ResponseEntity.ok("Création effectuée.");
        }
    }

    @PostMapping(value = "/modifIpV4")
    public ResponseEntity<?> modifIpv4(@Valid @RequestBody Ipv4ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterModifIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifIpV6")
    public ResponseEntity<?> modifIpv6(@Valid @RequestBody Ipv6ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterModifIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV4")
    public ResponseEntity<?> modifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterModifIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV6")
    public ResponseEntity<?> modifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException {
        return traiterModifIp(event,filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    public ResponseEntity<?> traiterModifIp(IpModifieeDTO ipModifieeDTO, String sirenFromSecurityContext){
        ipService.checkDoublonIpModifieeDto(ipModifieeDTO);
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                sirenFromSecurityContext,
                Long.parseLong(ipModifieeDTO.getId()),
                ipModifieeDTO.getIp(),
                ipModifieeDTO.getValidee(),
                ipModifieeDTO.getTypeAcces(),
                ipModifieeDTO.getTypeIp(),
                ipModifieeDTO.getCommentaires());
        log.info("IpController modification2");
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        log.info("IpController modification3");
        eventRepository.save(new EventEntity(ipModifieeEvent));
        String etab = etablissementRepository.getFirstBySiren(sirenFromSecurityContext).getName();
        String descriptionAcces = "id = " + ipModifieeDTO.getId() + ", ip ou plage d'ips = " + ipModifieeDTO.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        mailSender.send(emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, ipModifieeDTO.getCommentaires(), admin ));

        return ResponseEntity.ok("Modification effectuée.");
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
    public String delete(@Valid @RequestBody IpSupprimeeDTO ipSupprimeeDTO) throws SirenIntrouvableException, AccesInterditException {
        log.info("ipSupprimeeDTO.getId() = " + ipSupprimeeDTO.getId());
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getId(),
                filtrerAccesServices.getSirenFromSecurityContextUser());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
        return "L'accès a bien été supprimé";
    }
    @PostMapping(value = "/supprimeByAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public String deleteByAdmin(@Valid @RequestBody IpSupprimeeDTO ipSupprimeeDTO) throws SirenIntrouvableException, AccesInterditException {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getId(),
                ipSupprimeeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
        return "L'accès a bien été supprimé";
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
