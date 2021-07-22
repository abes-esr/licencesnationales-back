package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.dto.ip.*;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.exception.AccesInterditException;
import fr.abes.licencesnationales.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.repository.IpRepository;
import fr.abes.licencesnationales.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.service.EmailService;
import fr.abes.licencesnationales.service.IpService;
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
@RequestMapping("/v1/ln/ip")
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
    EmailService emailService;

    @Autowired
    IpService ipService;

    @Value("${ln.dest.notif.admin}")
    private String admin;

    private String demandeOk = "Votre demande a été prise en compte.";


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

    @PostMapping(value = "/adminAjoutIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminAjoutIpv4(@Valid @RequestBody Ipv4AjouteeDTO event)  {
        return traiterAjoutIp(event,event.getSiren());
    }

    @PostMapping(value = "/adminAjoutIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminAjoutIpv6(@Valid @RequestBody Ipv6AjouteeDTO event)  {
        return traiterAjoutIp(event,event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminAjoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDTO event)  {
        return traiterAjoutIp(event,event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminAjoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDTO event)  {
        return traiterAjoutIp(event,event.getSiren());
    }

    public ResponseEntity<?> traiterAjoutIp(IpAjouteeDTO event, String siren){
        log.info("ipService.checkDoublonIpAjouteeDto(fr.abes.licencesnationales.event).getStatusCodeValue() ="+ ipService.checkDoublonIpAjouteeDto(event).getStatusCodeValue());
        ResponseEntity doublonIp = ipService.checkDoublonIpAjouteeDto(event);
        if(doublonIp.getStatusCodeValue()==400){
            return doublonIp;
        }
        else {
            IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                    siren,
                    event.getTypeIp(),
                    event.getTypeAcces(),
                    event.getIp(),
                    event.getCommentaires());
            applicationEventPublisher.publishEvent(ipAjouteeEvent);
            eventRepository.save(new EventEntity(ipAjouteeEvent));

            String etab = etablissementRepository.getFirstBySiren(siren).getName();
            String descriptionAcces = " ip ou plage d'ips = " + event.getIp() + " en provenance de l'établissement " + etab;
            log.info("admin = " + admin);
            //mailSender.send(emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, fr.abes.licencesnationales.event.getCommentaires(), admin));
            emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, event.getCommentaires(), admin);

            return ResponseEntity.ok(demandeOk);
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

    @PostMapping(value = "/adminModifIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminModifIpv4(@Valid @RequestBody Ipv4ModifieeDTO event)  {
        return traiterModifIp(event,event.getSiren());
    }

    @PostMapping(value = "/adminModifIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminModifIpv6(@Valid @RequestBody Ipv6ModifieeDTO event)  {
        return traiterModifIp(event,event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminModifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDTO event)  {
        return traiterModifIp(event,event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> adminModifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDTO event)  {
        return traiterModifIp(event,event.getSiren());
    }

    public ResponseEntity<?> traiterModifIp(IpModifieeDTO ipModifieeDTO, String siren){
        log.info("ipService.checkDoublonIpModifieeDto(ipModifieeDTO).getStatusCodeValue() ="+ ipService.checkDoublonIpModifieeDto(ipModifieeDTO).getStatusCodeValue());
        ResponseEntity doublonIp = ipService.checkDoublonIpModifieeDto(ipModifieeDTO);
        if(doublonIp.getStatusCodeValue()==400){
            return doublonIp;
        }
        else {
            ipService.checkDoublonIpModifieeDto(ipModifieeDTO);
            IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                    siren,
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
            String etab = etablissementRepository.getFirstBySiren(siren).getName();
            String descriptionAcces = "id = " + ipModifieeDTO.getId() + ", ip ou plage d'ips = " + ipModifieeDTO.getIp() + " en provenance de l'établissement " + etab;
            log.info("admin = " + admin);
            emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, ipModifieeDTO.getCommentaires(), admin);

            return ResponseEntity.ok(demandeOk);
        }
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
        return demandeOk;
    }
    @PostMapping(value = "/supprimeByAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public String deleteByAdmin(@Valid @RequestBody IpSupprimeeDTO ipSupprimeeDTO) throws SirenIntrouvableException, AccesInterditException {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getId(),
                ipSupprimeeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
        return demandeOk;
    }

    @GetMapping(value = "/{siren}")
    public Set<IpEntity> get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        log.info("get - ");
        filtrerAccesServices.autoriserServicesParSiren(siren);
        return etablissementRepository.getFirstBySiren(siren).getIps();
    }
    @GetMapping(value = "/ipsEtab/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public Set<IpEntity> getIpsEtab(@PathVariable String siren) {
        log.info("getIpsEtab");
        return etablissementRepository.getFirstBySiren(siren).getIps();
    }

    @PostMapping(value = "/getIpEntity")
    public IpEntity getIpEntity(@RequestBody IpDTO ipDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipDTO.getSiren());
        Long identifiant = Long.parseLong(ipDTO.getId());
        return ipRepository.getFirstById(identifiant);
    }
}
