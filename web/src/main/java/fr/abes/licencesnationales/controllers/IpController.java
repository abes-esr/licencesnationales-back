package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.dto.ip.*;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.exception.AccesInterditException;
import fr.abes.licencesnationales.exception.IpException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;
import java.util.Set;

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
    public EventEntity ajoutIpv4(@Valid @RequestBody Ipv4AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutIpV6")
    public EventEntity ajoutIpv6(@Valid @RequestBody Ipv6AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV4")
    public EventEntity ajoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV6")
    public EventEntity ajoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminAjoutIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminAjoutIpv4(@Valid @RequestBody Ipv4AjouteeDTO event) throws IpException {
        return traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminAjoutIpv6(@Valid @RequestBody Ipv6AjouteeDTO event) throws IpException {
        return traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminAjoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDTO event) throws IpException {
        return traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminAjoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDTO event) throws IpException {
        return traiterAjoutIp(event, event.getSiren());
    }

    public EventEntity traiterAjoutIp(IpAjouteeDTO event, String siren) throws IpException {
        ipService.checkDoublonIpAjouteeDto(event);

        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                siren,
                event.getTypeIp(),
                event.getTypeAcces(),
                event.getIp(),
                event.getCommentaires());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        EventEntity eventEntity = eventRepository.save(new EventEntity(ipAjouteeEvent));

        String etab = etablissementRepository.getFirstBySiren(siren).getName();
        String descriptionAcces = " ip ou plage d'ips = " + event.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        //mailSender.send(emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, fr.abes.licencesnationales.event.getCommentaires(), admin));
        emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, event.getCommentaires(), admin);

        return eventEntity;
    }

    @PostMapping(value = "/modifIpV4")
    public EventEntity modifIpv4(@Valid @RequestBody Ipv4ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifIpV6")
    public EventEntity modifIpv6(@Valid @RequestBody Ipv6ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV4")
    public EventEntity modifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV6")
    public EventEntity modifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        return traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminModifIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminModifIpv4(@Valid @RequestBody Ipv4ModifieeDTO event) throws IpException {
        return traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminModifIpv6(@Valid @RequestBody Ipv6ModifieeDTO event) throws IpException {
        return traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminModifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDTO event) throws IpException {
        return traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity adminModifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDTO event) throws IpException {
        return traiterModifIp(event, event.getSiren());
    }

    public EventEntity traiterModifIp(IpModifieeDTO ipModifieeDTO, String siren) throws IpException {
        ipService.checkDoublonIpModifieeDto(ipModifieeDTO);
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                siren,
                Long.parseLong(ipModifieeDTO.getId()),
                ipModifieeDTO.getIp(),
                ipModifieeDTO.getValidee(),
                ipModifieeDTO.getTypeAcces(),
                ipModifieeDTO.getTypeIp(),
                ipModifieeDTO.getCommentaires());
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        EventEntity eventEntity = eventRepository.save(new EventEntity(ipModifieeEvent));
        String etab = etablissementRepository.getFirstBySiren(siren).getName();
        String descriptionAcces = "id = " + ipModifieeDTO.getId() + ", ip ou plage d'ips = " + ipModifieeDTO.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, ipModifieeDTO.getCommentaires(), admin);

        return eventEntity;
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
