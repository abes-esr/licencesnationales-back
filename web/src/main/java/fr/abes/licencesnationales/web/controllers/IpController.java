package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.*;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.exception.UnknownIpException;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.export.ExportIp;
import fr.abes.licencesnationales.web.dto.etablissement.StatsDto;
import fr.abes.licencesnationales.web.dto.ip.IpHistoriqueDto;
import fr.abes.licencesnationales.web.dto.ip.IpSearchWebDto;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import fr.abes.licencesnationales.web.dto.ip.creation.IpAjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.creation.IpCreeResultWebDto;
import fr.abes.licencesnationales.web.dto.ip.gestion.IpGereeWebDto;
import fr.abes.licencesnationales.web.exception.InvalidTokenException;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import lombok.extern.java.Log;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log
@RestController
@RequestMapping("/v1/ip")
public class IpController extends AbstractController {
    @Autowired
    private IpEventRepository eventRepository;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private IpService ipService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FiltrerAccesServices filtrerAccesServices;

    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private ExportIp exportIp;

    @Value("${ln.dest.notif.admin}")
    private String admin;


    @PutMapping(value = "/{siren}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> ajoutIp(@Valid @RequestBody IpAjouteeWebDto dto, @PathVariable String siren) throws SirenIntrouvableException, AccesInterditException, IpException, InvalidTokenException {
        filtrerAccesServices.autoriserServicesParSiren(siren);
        IpCreeResultWebDto dtoResult = new IpCreeResultWebDto();
        IpCreeEventEntity ipAjouteeEvent = mapper.map(dto, IpCreeEventEntity.class);
        ipAjouteeEvent.setSource(this);
        ipAjouteeEvent.setSiren(siren);
        try {
            applicationEventPublisher.publishEvent(ipAjouteeEvent);
            eventRepository.save(ipAjouteeEvent);
            Integer ipId = ipService.getIdByIp(ipAjouteeEvent.getIp());
            dtoResult.setId(ipId);
        } catch (Exception exception) {
            throw new IpException(exception.getLocalizedMessage());
        }
        dtoResult.setMessage(Constant.MESSAGE_AJOUTIP_OK);
        return buildResponseEntity(dtoResult);

    }

    @PostMapping(value = "/gerer/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> gererIp(@PathVariable String siren, @RequestBody List<IpGereeWebDto> ips) {
        List<Map<String, String>> resultat = new ArrayList<>();
        ips.forEach(ip -> {
            Map<String, String> result = new HashMap<>();
            try {
                Integer id = ip.getIdIp();
                IpEntity ipInBdd = ipService.getFirstById(id);
                result.put("ip", ipInBdd.getIp());
                switch (ip.getAction()) {
                    case VALIDER:
                        IpValideeEventEntity ipValideeEvent = mapper.map(ipInBdd, IpValideeEventEntity.class);
                        applicationEventPublisher.publishEvent(ipValideeEvent);
                        eventRepository.save(ipValideeEvent);
                        result.put("action", "validation");
                        break;
                    case REJETER:
                        IpRejeteeEventEntity ipRejeteeEvent = mapper.map(ipInBdd, IpRejeteeEventEntity.class);
                        applicationEventPublisher.publishEvent(ipRejeteeEvent);
                        eventRepository.save(ipRejeteeEvent);
                        result.put("action", "rejet");
                        break;
                    case SUPPRIMER:
                        IpSupprimeeEventEntity ipSupprimeeEvent = mapper.map(ipInBdd, IpSupprimeeEventEntity.class);
                        ipSupprimeeEvent.setCommentaire(ip.getCommentaire());
                        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
                        eventRepository.save(ipSupprimeeEvent);
                        result.put("action", "suppression");
                        result.put("commentaire", ip.getCommentaire());
                        break;
                    default:
                        result.put("action", "action inconnue");
                }
            } catch (Exception e) {
                result.put("erreur", e.getMessage());
            }
            resultat.add(result);
        });
        //agencement de la map des résultats par action / IP au lieu de IP / action
        Map<String, List<String>> mapResultat = new HashMap<>();
        List<String> listValidation = new ArrayList<>();
        List<String> listSuppression = new ArrayList<>();
        List<String> listRejet = new ArrayList<>();
        List<String> listErreur = new ArrayList<>();
        resultat.forEach(r -> {
            if (r.get("erreur") != null) {
                listErreur.add(r.get("erreur"));
            }
            else {
                switch (r.get("action")) {
                    case "validation":
                        listValidation.add(r.get("ip"));
                        break;
                    case "suppression":
                        listSuppression.add(r.get("ip") + "|" + r.get("commentaire"));
                        break;
                    case "rejet":
                        listRejet.add(r.get("ip"));
                        break;
                    default:
                }
            }
        });
        mapResultat.put("validation", listValidation);
        mapResultat.put("suppression", listSuppression);
        mapResultat.put("rejet", listRejet);
        mapResultat.put("erreur", listErreur);
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        emailService.constructBilanRecapActionIpUser(etab.getContact().getMail(), etab.getNom(), mapResultat);
        return buildResponseEntity(resultat);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) throws UnknownIpException, SirenIntrouvableException, AccesInterditException {
        String siren = filtrerAccesServices.getSirenFromSecurityContextUser();
        IpEntity ip = ipService.getFirstById(id);
        if(!"admin".equals(filtrerAccesServices.getRoleFromSecurityContextUser()) && !ip.getEtablissement().getSiren().equals(siren)) {
            throw new AccesInterditException(Constant.ACCES_INTERDIT);
        }
        IpSupprimeeEventEntity ipSupprimeeEvent = mapper.map(ip, IpSupprimeeEventEntity.class);
        applicationEventPublisher.publishEvent(ipSupprimeeEvent);
        eventRepository.save(ipSupprimeeEvent);
        return buildResponseEntity(Constant.MESSAGE_SUPPIP_OK);
    }

    @GetMapping(value = "/{siren}")
    public Set<IpWebDto> get(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException, InvalidTokenException {
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
    public IpWebDto getIpEntity(@RequestBody IpWebDto ipDto) throws SirenIntrouvableException, AccesInterditException, UnknownIpException, InvalidTokenException {
        filtrerAccesServices.autoriserServicesParSiren(filtrerAccesServices.getSirenFromSecurityContextUser());
        return mapper.map(ipService.getFirstById(ipDto.getId()), IpWebDto.class);
    }

    @PostMapping(value = "/export/{siren}")
    public void exportIp(@PathVariable String siren, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=\"export.csv\"");
        InputStream stream;
        List<String> sirens = new ArrayList<>();
        sirens.add(siren);
        stream = exportIp.generateCsv(sirens);
        IOUtils.copy(stream, response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping(value = "/whois/{ip}")
    public String whoIs(@PathVariable String ip) throws Exception {
        return ipService.whoIs(ip);
    }

    @PostMapping(value = "/search")
    @PreAuthorize("hasAuthority('admin')")
    public List<IpSearchWebDto> search(@RequestBody List<String> criteres) {
        return mapper.mapList(ipService.search(criteres), IpSearchWebDto.class);
    }

    @GetMapping(value = "/histo/{siren}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<IpHistoriqueDto> historique(@PathVariable String siren) {
        List<IpHistoriqueDto> listHisto = new ArrayList<>();
        for (IpEventEntity e : eventService.getHistoIp(siren)) {
            IpHistoriqueDto h = mapper.map(e, IpHistoriqueDto.class);
            h.setEvent(e.getDecriminatorValue());
            listHisto.add(h);
        }
        return listHisto;
    }

    @GetMapping(value = "/stats")
    @PreAuthorize("hasAnyAuthority('admin')")
    public StatsDto statistiques(@RequestParam String dateDebut, @RequestParam String dateFin) throws ParseException {
        int ipCrees, ipValides, ipRejetees, ipSupprimees;
        ipCrees = ipValides = ipRejetees = ipSupprimees = 0;

        Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateDebut);
        Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(dateFin);

        for (IpEventEntity e : eventService.getHistoAllIp(date1, date2)) {
            switch (e.getDecriminatorValue()) {
                case "cree":
                    ipCrees++;
                    break;
                case "valide":
                    ipValides++;
                    break;
                case "supprime":
                    ipSupprimees++;
                    break;
                case "rejete":
                    ipRejetees++;
                    break;
                default:
                    break;
            }
        }
        StatsDto stats = new StatsDto();
        stats.ajouterStat("Creations", ipCrees);
        stats.ajouterStat("Validations", ipValides);
        stats.ajouterStat("Suppressions", ipSupprimees);
        stats.ajouterStat("Rejets", ipRejetees);

        return stats;
    }

}
