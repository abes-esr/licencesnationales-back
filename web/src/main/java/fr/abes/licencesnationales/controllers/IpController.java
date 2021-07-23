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
    public void ajoutIpv4(@Valid @RequestBody Ipv4AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutIpV6")
    public void ajoutIpv6(@Valid @RequestBody Ipv6AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV4")
    public void ajoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV6")
    public void ajoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminAjoutIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv4(@Valid @RequestBody Ipv4AjouteeDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv6(@Valid @RequestBody Ipv6AjouteeDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    public void traiterAjoutIp(IpAjouteeDTO event, String siren) throws IpException {
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

    }

    @PostMapping(value = "/modifIpV4")
    public void modifIpv4(@Valid @RequestBody Ipv4ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifIpV6")
    public void modifIpv6(@Valid @RequestBody Ipv6ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV4")
    public void modifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV6")
    public void modifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminModifIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv4(@Valid @RequestBody Ipv4ModifieeDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv6(@Valid @RequestBody Ipv6ModifieeDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    public void traiterModifIp(IpModifieeDTO ipModifieeDTO, String siren) throws IpException {
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

    }


    @PostMapping(value = "/valide")
    public void validate(@RequestBody IpValideeDTO ipValideeDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipValideeDTO.getSiren());
        IpValideeEvent ipValideeEvent = new IpValideeEvent(this,
                ipValideeDTO.getIp(),
                ipValideeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipValideeEvent);
        eventRepository.save(new EventEntity(ipValideeEvent));
    }

    @DeleteMapping(value = "/supprime")
    public void delete(@Valid @RequestBody IpSupprimeeDTO ipSupprimeeDTO) throws SirenIntrouvableException, AccesInterditException {
        log.info("ipSupprimeeDTO.getId() = " + ipSupprimeeDTO.getId());
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getId(),
                filtrerAccesServices.getSirenFromSecurityContextUser());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
    }

    @DeleteMapping(value = "/supprimeByAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public void deleteByAdmin(@Valid @RequestBody IpSupprimeeDTO ipSupprimeeDTO) throws SirenIntrouvableException, AccesInterditException {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDTO.getId(),
                ipSupprimeeDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
    }

    @GetMapping(value = "/{siren}")
    public Set<IpEntity> get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        return etablissementRepository.getFirstBySiren(siren).getIps();
    }

    @GetMapping(value = "/ipsEtab/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public Set<IpEntity> getIpsEtab(@PathVariable String siren) {
        return etablissementRepository.getFirstBySiren(siren).getIps();
    }

    @PostMapping(value = "/getIpEntity")
    public IpEntity getIpEntity(@RequestBody IpDTO ipDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipDTO.getSiren());
        Long identifiant = Long.parseLong(ipDTO.getId());
        return ipRepository.getFirstById(identifiant);
    }
}
