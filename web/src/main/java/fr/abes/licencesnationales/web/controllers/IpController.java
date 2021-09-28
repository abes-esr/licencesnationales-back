package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpModifieeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpValideeEventEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.exception.UnknownIpException;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import fr.abes.licencesnationales.web.dto.ip.cree.IpAjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.modifie.IpModifieeWebDto;
import fr.abes.licencesnationales.web.dto.ip.modifie.IpModifieeUserWebDto;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Log
@RestController
@RequestMapping("/v1/ip")
public class IpController {
    @Autowired
    private IpEventRepository eventRepository;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private IpService ipService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FiltrerAccesServices filtrerAccesServices;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private UtilsMapper mapper;

    @Value("${ln.dest.notif.admin}")
    private String admin;


    @PutMapping(value = "/{siren}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void ajoutIp(@Valid @RequestBody List<IpAjouteeWebDto> dto, @PathVariable String siren, HttpServletRequest request) throws SirenIntrouvableException, AccesInterditException, IpException {
        List<String> errors = new ArrayList<>();
        filtrerAccesServices.autoriserServicesParSiren(siren);
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        String etab = etablissementService.getFirstBySiren(siren).getName();
        dto.forEach(e -> {
            IpCreeEventEntity ipAjouteeEvent = mapper.map(e, IpCreeEventEntity.class);
            ipAjouteeEvent.setSource(this);
            ipAjouteeEvent.setSiren(siren);
            try {
                applicationEventPublisher.publishEvent(ipAjouteeEvent);
                eventRepository.save(ipAjouteeEvent);
            } catch (Exception exception) {
                errors.add(exception.getLocalizedMessage());
            }
            String descriptionAcces = " ip ou plage d'ips = " + e.getIp() + " en provenance de l'établissement " + etab;
            emailService.constructAccesCreeEmail(locale, descriptionAcces, e.getCommentaires(), admin);
        });

        if (errors.size() > 0) {
            throw new IpException(errors.toString());
        }

    }

    @PostMapping(value = "/{id}")
    public void modifIp(@PathVariable Integer id, @Valid @RequestBody IpModifieeWebDto dto, HttpServletRequest request) throws SirenIntrouvableException, AccesInterditException, UnknownIpException {
        EtablissementEntity etab = ipService.getEtablissementByIp(id);
        filtrerAccesServices.autoriserServicesParSiren(etab.getSiren());
        if (dto instanceof IpModifieeUserWebDto) {
            if (!filtrerAccesServices.getSirenFromSecurityContextUser().equals(etab.getSiren())) {
                throw new AccesInterditException("Impossible de modifier un autre établissement que celui de l'utilisateur");
            }
        } else {
            if (!("admin").equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
                throw new AccesInterditException("L'opération ne peut être effectuée que par un administrateur");
            }
        }
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        IpModifieeEventEntity ipModifieeEvent = mapper.map(dto, IpModifieeEventEntity.class);
        ipModifieeEvent.setSource(this);
        ipModifieeEvent.setSiren(etab.getSiren());
        applicationEventPublisher.publishEvent(ipModifieeEvent);
        eventRepository.save(ipModifieeEvent);
        String descriptionAcces = "id = " + id + ", ip ou plage d'ips = " + dto.getIp() + " en provenance de l'établissement " + etab.getSiren();
        emailService.constructAccesModifieEmail(locale, descriptionAcces, dto.getCommentaires(), admin);
    }

    @PostMapping(value = "/valider")
    @PreAuthorize("hasAuthority('admin')")
    public void validate(@RequestBody List<Integer> ids) throws SirenIntrouvableException, AccesInterditException {
        ids.forEach(id -> {
            IpValideeEventEntity ipValideeEvent = new IpValideeEventEntity(this, id);
            applicationEventPublisher.publishEvent(ipValideeEvent);
            eventRepository.save(ipValideeEvent);
        });

    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Integer id) {
        IpSupprimeeEventEntity ipSupprimeeEvent = new IpSupprimeeEventEntity(this, id);
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(ipSupprimeeEvent);
    }

    @GetMapping(value = "/{siren}")
    public Set<IpWebDto> get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        Set<IpEntity> setIps = etablissementService.getFirstBySiren(siren).getIps();
        return mapper.mapSet(setIps, IpWebDto.class);
    }

    @GetMapping(value = "/ipsEtab/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public List<IpWebDto> getIpsEtab(@PathVariable String siren) {
        Set<IpEntity> setIps = etablissementService.getFirstBySiren(siren).getIps();
        List<IpEntity> listIps = List.copyOf(setIps);
        return mapper.mapList(listIps, IpWebDto.class);
    }

    @PostMapping(value = "/getIpEntity")
    public IpWebDto getIpEntity(@RequestBody IpWebDto ipDto) throws SirenIntrouvableException, AccesInterditException, UnknownIpException {
        filtrerAccesServices.autoriserServicesParSiren(filtrerAccesServices.getSirenFromSecurityContextUser());
        return mapper.map(ipService.getFirstById(ipDto.getId()), IpWebDto.class);
    }
}
