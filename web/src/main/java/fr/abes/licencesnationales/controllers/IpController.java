package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.converter.UtilsMapper;
import fr.abes.licencesnationales.dto.IpWebDto;
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
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.repository.IpRepository;
import fr.abes.licencesnationales.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.services.EmailService;
import fr.abes.licencesnationales.services.EtablissementService;
import fr.abes.licencesnationales.services.IpService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Log
@RestController
@RequestMapping("/v1/ln/ip")
public class IpController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FiltrerAccesServices filtrerAccesServices;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IpService ipService;

    @Autowired
    private UtilsMapper mapper;

    @Value("${ln.dest.notif.admin}")
    private String admin;


    @PostMapping(value = "/ajoutIpV4")
    public void ajoutIpv4(@Valid @RequestBody Ipv4AjouteeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutIpV6")
    public void ajoutIpv6(@Valid @RequestBody Ipv6AjouteeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV4")
    public void ajoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV6")
    public void ajoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminAjoutIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv4(@Valid @RequestBody Ipv4AjouteeEventDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv6(@Valid @RequestBody Ipv6AjouteeEventDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeEventDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeEventDTO event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    public void traiterAjoutIp(IpAjouteeEventDTO event, String siren) throws IpException, RestClientException {
        ipService.checkDoublonIpAjouteeDto(event);

        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                siren,
                event.getTypeIp(),
                event.getTypeAcces(),
                event.getIp(),
                event.getCommentaires());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        EventEntity eventEntity = eventRepository.save(new EventEntity(ipAjouteeEvent));

        String etab = etablissementService.getFirstBySiren(siren).getName();
        String descriptionAcces = " ip ou plage d'ips = " + event.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        //mailSender.send(emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, fr.abes.licencesnationales.event.getCommentaires(), admin));
        emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, event.getCommentaires(), admin);

    }

    @PostMapping(value = "/modifIpV4")
    public void modifIpv4(@Valid @RequestBody Ipv4ModifieeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifIpV6")
    public void modifIpv6(@Valid @RequestBody Ipv6ModifieeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV4")
    public void modifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV6")
    public void modifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeEventDTO event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminModifIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv4(@Valid @RequestBody Ipv4ModifieeEventDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv6(@Valid @RequestBody Ipv6ModifieeEventDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeEventDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeEventDTO event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    public void traiterModifIp(IpModifieeEventDTO ipModifieeEventDTO, String siren) throws IpException, RestClientException {
        ipService.checkDoublonIpModifieeDto(ipModifieeEventDTO);
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                siren,
                Long.parseLong(ipModifieeEventDTO.getId()),
                ipModifieeEventDTO.getIp(),
                ipModifieeEventDTO.getValidee(),
                ipModifieeEventDTO.getTypeAcces(),
                ipModifieeEventDTO.getTypeIp(),
                ipModifieeEventDTO.getCommentaires());
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        EventEntity eventEntity = eventRepository.save(new EventEntity(ipModifieeEvent));
        String etab = etablissementService.getFirstBySiren(siren).getName();
        String descriptionAcces = "id = " + ipModifieeEventDTO.getId() + ", ip ou plage d'ips = " + ipModifieeEventDTO.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, ipModifieeEventDTO.getCommentaires(), admin);

    }


    @PostMapping(value = "/valide")
    public void validate(@RequestBody IpValideeEventDTO ipValideeEventDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipValideeEventDTO.getSiren());
        IpValideeEvent ipValideeEvent = new IpValideeEvent(this,
                ipValideeEventDTO.getIp(),
                ipValideeEventDTO.getSiren());
        applicationEventPublisher.publishEvent(ipValideeEvent);
        eventRepository.save(new EventEntity(ipValideeEvent));
    }

    @DeleteMapping(value = "/supprime")
    public void delete(@Valid @RequestBody IpSupprimeeEventDTO ipSupprimeeEventDTO) throws SirenIntrouvableException, AccesInterditException {
        log.info("ipSupprimeeDTO.getId() = " + ipSupprimeeEventDTO.getId());
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeEventDTO.getId(),
                filtrerAccesServices.getSirenFromSecurityContextUser());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
    }

    @DeleteMapping(value = "/supprimeByAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public void deleteByAdmin(@Valid @RequestBody IpSupprimeeEventDTO ipSupprimeeEventDTO) throws SirenIntrouvableException, AccesInterditException {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeEventDTO.getId(),
                ipSupprimeeEventDTO.getSiren());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
    }

    @GetMapping(value = "/{siren}")
    public List<IpWebDto> get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        Set<IpEntity> setIps = etablissementService.getFirstBySiren(siren).getIps();
        List<IpEntity> listIps = List.copyOf(setIps);
        return mapper.mapList(listIps, IpWebDto.class);
    }

    @GetMapping(value = "/ipsEtab/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public List<IpWebDto> getIpsEtab(@PathVariable String siren) {
        Set<IpEntity> setIps = etablissementService.getFirstBySiren(siren).getIps();
        List<IpEntity> listIps = List.copyOf(setIps);
        return mapper.mapList(listIps, IpWebDto.class);
    }

    @PostMapping(value = "/getIpEntity")
    public IpWebDto getIpEntity(@RequestBody IpEventDTO ipEventDTO) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipEventDTO.getSiren());
        Long identifiant = Long.parseLong(ipEventDTO.getId());
        return mapper.map(ipRepository.getFirstById(identifiant), IpWebDto.class);
    }
}
