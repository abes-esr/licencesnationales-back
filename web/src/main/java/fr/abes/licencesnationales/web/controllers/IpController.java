package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.ip.*;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import fr.abes.licencesnationales.web.dto.ip.*;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.entities.IpEntity;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.core.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.core.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.repository.IpRepository;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
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
    public void ajoutIpv4(@Valid @RequestBody Ipv4AjouteeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutIpV6")
    public void ajoutIpv6(@Valid @RequestBody Ipv6AjouteeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV4")
    public void ajoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV6")
    public void ajoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminAjoutIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv4(@Valid @RequestBody Ipv4AjouteeDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv6(@Valid @RequestBody Ipv6AjouteeDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    public void traiterAjoutIp(IpAjouteeDto event, String siren) throws IpException, RestClientException {
        ipService.checkDoublonIpAjouteeDto(event);

        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this,
                siren,
                event.getTypeIp(),
                event.getTypeAcces(),
                event.getIp(),
                event.getCommentaires());
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        eventRepository.save(new EventEntity(ipAjouteeEvent));

        String etab = etablissementService.getFirstBySiren(siren).getName();
        String descriptionAcces = " ip ou plage d'ips = " + event.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        //mailSender.send(emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, fr.abes.licencesnationales.core.event.getCommentaires(), admin));
        emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, event.getCommentaires(), admin);

    }

    @PostMapping(value = "/modifIpV4")
    public void modifIpv4(@Valid @RequestBody Ipv4ModifieeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifIpV6")
    public void modifIpv6(@Valid @RequestBody Ipv6ModifieeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV4")
    public void modifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV6")
    public void modifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminModifIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv4(@Valid @RequestBody Ipv4ModifieeDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv6(@Valid @RequestBody Ipv6ModifieeDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    public void traiterModifIp(IpModifieeDto ipModifieeDto, String siren) throws IpException, RestClientException {
        ipService.checkDoublonIpModifieeDto(ipModifieeDto);
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,
                siren,
                ipModifieeDto.getIp(),
                ipModifieeDto.isValidee(),
                ipModifieeDto.getTypeAcces(),
                ipModifieeDto.getTypeIp(),
                ipModifieeDto.getCommentaires());
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        eventRepository.save(new EventEntity(ipModifieeEvent));
        String etab = etablissementService.getFirstBySiren(siren).getName();
        String descriptionAcces = "id = " + ipModifieeDto.getId() + ", ip ou plage d'ips = " + ipModifieeDto.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        emailService.constructAccesModifieEmail(new Locale("fr", "FR"), descriptionAcces, ipModifieeDto.getCommentaires(), admin);

    }


    @PostMapping(value = "/valide")
    public void validate(@RequestBody IpValideeDto ipValideeDto) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipValideeDto.getSiren());
        IpValideeEvent ipValideeEvent = new IpValideeEvent(this,
                ipValideeDto.getIp(),
                ipValideeDto.getSiren());
        applicationEventPublisher.publishEvent(ipValideeEvent);
        eventRepository.save(new EventEntity(ipValideeEvent));
    }

    @DeleteMapping(value = "/supprime")
    public void delete(@Valid @RequestBody IpSupprimeeDto ipSupprimeeDto) throws SirenIntrouvableException, AccesInterditException {
        log.info("ipSupprimeeDTO.getId() = " + ipSupprimeeDto.getId());
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDto.getId(),
                filtrerAccesServices.getSirenFromSecurityContextUser());
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(new EventEntity(ipSupprimeeEvent));
    }

    @DeleteMapping(value = "/supprimeByAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public void deleteByAdmin(@Valid @RequestBody IpSupprimeeDto ipSupprimeeDto) throws SirenIntrouvableException, AccesInterditException {
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this,
                ipSupprimeeDto.getId(),
                ipSupprimeeDto.getSiren());
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
    public IpWebDto getIpEntity(@RequestBody IpDto ipDto) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipDto.getSiren());
        Long identifiant = Long.parseLong(ipDto.getId());
        return mapper.map(ipRepository.getFirstById(identifiant), IpWebDto.class);
    }
}
