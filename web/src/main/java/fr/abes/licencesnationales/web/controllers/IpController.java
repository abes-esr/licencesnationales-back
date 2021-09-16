package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.*;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.dto.ip.*;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Log
@RestController
@RequestMapping("/v1/ln/ip")
public class IpController {
    @Autowired
    private IpEventRepository eventRepository;

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
    private UtilsMapper mapper;

    @Value("${ln.dest.notif.admin}")
    private String admin;


    @PostMapping(value = "/ajoutIpV4")
    public void ajoutIpv4(@Valid @RequestBody Ipv4AjouteeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutIpV6")
    public void ajoutIpv6(@Valid @RequestBody Ipv6AjouteeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV4")
    public void ajoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/ajoutPlageIpV6")
    public void ajoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterAjoutIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminAjoutIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv4(@Valid @RequestBody Ipv4AjouteeWebDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutIpv6(@Valid @RequestBody Ipv6AjouteeWebDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv4(@Valid @RequestBody PlageIpv4AjouteeWebDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminAjoutPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminAjoutPlageIpv6(@Valid @RequestBody PlageIpv6AjouteeWebDto event) throws IpException {
        traiterAjoutIp(event, event.getSiren());
    }

    public void traiterAjoutIp(IpAjouteeWebDto event, String siren) throws IpException, RestClientException {
        IpCreeEventEntity ipAjouteeEvent = mapper.map(event, IpCreeEventEntity.class);
        applicationEventPublisher.publishEvent(ipAjouteeEvent);
        eventRepository.save(ipAjouteeEvent);

        String etab = etablissementService.getFirstBySiren(siren).getName();
        String descriptionAcces = " ip ou plage d'ips = " + event.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        //mailSender.send(emailService.constructAccesCreeEmail(new Locale("fr", "FR"), descriptionAcces, fr.abes.licencesnationales.core.event.getCommentaires(), admin));
        emailService.constructAccesCreeEmail(descriptionAcces, event.getCommentaires(), admin);

    }

    @PostMapping(value = "/modifIpV4")
    public void modifIpv4(@Valid @RequestBody Ipv4ModifieeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifIpV6")
    public void modifIpv6(@Valid @RequestBody Ipv6ModifieeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV4")
    public void modifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/modifPlageIpV6")
    public void modifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeWebDto event) throws SirenIntrouvableException, AccesInterditException, IpException {
        traiterModifIp(event, filtrerAccesServices.getSirenFromSecurityContextUser());
    }

    @PostMapping(value = "/adminModifIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv4(@Valid @RequestBody Ipv4ModifieeWebDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifIpv6(@Valid @RequestBody Ipv6ModifieeWebDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV4")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv4(@Valid @RequestBody PlageIpv4ModifieeWebDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    @PostMapping(value = "/adminModifPlageIpV6")
    @PreAuthorize("hasAuthority('admin')")
    public void adminModifPlageIpv6(@Valid @RequestBody PlageIpv6ModifieeWebDto event) throws IpException {
        traiterModifIp(event, event.getSiren());
    }

    public void traiterModifIp(IpModifieeWebDto ipModifieeDto, String siren) throws IpException, RestClientException {
        IpModifieeEventEntity ipModifieeEvent = mapper.map(ipModifieeDto, IpModifieeEventEntity.class);
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        eventRepository.save(ipModifieeEvent);
        String etab = etablissementService.getFirstBySiren(siren).getName();
        String descriptionAcces = "id = " + ipModifieeDto.getId() + ", ip ou plage d'ips = " + ipModifieeDto.getIp() + " en provenance de l'établissement " + etab;
        log.info("admin = " + admin);
        emailService.constructAccesModifieEmail(descriptionAcces, ipModifieeDto.getCommentaires(), admin);

    }


    @PostMapping(value = "/valide")
    public void validate(@RequestBody IpValideeWebDto ipValideeDto) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipValideeDto.getSiren());
        IpValideeEventEntity ipValideeEvent = mapper.map(ipValideeDto, IpValideeEventEntity.class);
        applicationEventPublisher.publishEvent(ipValideeEvent);
        eventRepository.save(ipValideeEvent);
    }

    @DeleteMapping(value = "/supprime")
    public void delete(@Valid @RequestBody IpSupprimeeWebDto ipSupprimeeDto) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(ipSupprimeeDto.getSiren());
        IpSupprimeeEventEntity ipSupprimeeEvent = mapper.map(ipSupprimeeDto, IpSupprimeeEventEntity.class);
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(ipSupprimeeEvent);
    }

    @DeleteMapping(value = "/supprimeByAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public void deleteByAdmin(@Valid @RequestBody IpSupprimeeWebDto ipSupprimeeDto) {
        IpSupprimeeEventEntity ipSupprimeeEvent = mapper.map(ipSupprimeeDto, IpSupprimeeEventEntity.class);
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(ipSupprimeeEvent);
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
    public IpWebDto getIpEntity(@RequestBody IpWebDto ipDto) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(filtrerAccesServices.getSirenFromSecurityContextUser());
        return mapper.map(ipRepository.getFirstById(ipDto.getId()), IpWebDto.class);
    }
}
