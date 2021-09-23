package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.*;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import fr.abes.licencesnationales.core.exception.*;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.*;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.exception.CaptchaException;
import fr.abes.licencesnationales.web.exception.InvalidTokenException;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/v1/etablissements")
public class EtablissementController {

    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    private FiltrerAccesServices filtrerAccesServices;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Value("${ln.dest.notif.admin}")
    private String admin;


    @PutMapping
    public void creationCompte(@Valid @RequestBody EtablissementCreeWebDto etablissementCreeWebDto, HttpServletRequest request) throws CaptchaException, SirenExistException, MailDoublonException, RestClientException {
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        String captcha = etablissementCreeWebDto.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException("Le champs 'recaptcha' est obligatoire");
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, "creationCompte");
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        // On convertit la DTO web (Json) en objet métier d'événement de création d'établissement
        EtablissementCreeEventEntity event = mapper.map(etablissementCreeWebDto, EtablissementCreeEventEntity.class);
        event.setSource(this);
        // On genère un identifiant Abes
        event.setIdAbes(GenererIdAbes.generateId());
        // On crypte le mot de passe
        event.setMotDePasse(passwordEncoder.encode(etablissementCreeWebDto.getContact().getMotDePasse()));

        // On publie l'événement et on le sauvegarde
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);

        /*******************************************/
        emailService.constructCreationCompteEmailUser(locale, etablissementCreeWebDto.getContact().getMail());
        emailService.constructCreationCompteEmailAdmin(locale, admin, etablissementCreeWebDto.getSiren(), etablissementCreeWebDto.getName());
    }

    @PostMapping(value = "")
    public void edit(@Valid @RequestBody EtablissementModifieWebDto etablissementModifieWebDto) throws SirenIntrouvableException, AccesInterditException {
        if (etablissementModifieWebDto instanceof EtablissementModifieUserWebDto) {
            etablissementModifieWebDto.setSiren(filtrerAccesServices.getSirenFromSecurityContextUser());
        } else {
            if (!("admin").equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
                throw new AccesInterditException("L'opération ne peut être effectuée que par un administrateur");
            }
        }
        EtablissementModifieEventEntity event = mapper.map(etablissementModifieWebDto, EtablissementModifieEventEntity.class);
        event.setSource(this);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public void fusion(@RequestBody EtablissementFusionneWebDto etablissementFusionneWebDto) throws JsonProcessingException {
        EtablissementFusionneEventEntity etablissementFusionneEvent = mapper.map(etablissementFusionneWebDto, EtablissementFusionneEventEntity.class);
        etablissementFusionneEvent.setAnciensEtablissementsInBdd(objectMapper.writeValueAsString(etablissementFusionneEvent.getSirenAnciensEtablissements()));
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        eventService.save(etablissementFusionneEvent);
    }

    @PostMapping(value = "/scission")
    @PreAuthorize("hasAuthority('admin')")
    public void division(@RequestBody EtablissementDiviseWebDto etablissementDiviseWebDto) throws UnknownTypeEtablissementException, JsonProcessingException, UnknownStatutException {
        EtablissementDiviseEventEntity etablissementDiviseEvent = mapper.map(etablissementDiviseWebDto, EtablissementDiviseEventEntity.class);
        TypeEtablissementEntity type = referenceService.findTypeEtabByLibelle(etablissementDiviseEvent.getTypeEtablissement());
        StatutEtablissementEntity statut = (StatutEtablissementEntity) referenceService.findStatutById(Constant.STATUT_ETAB_NOUVEAU);
        //on initialise le statut des nouveaux établissement et on génère l'Id Abes
        etablissementDiviseEvent.getEtablissementDivises().forEach(e -> {
            e.setIdAbes(GenererIdAbes.generateId());
            e.setTypeEtablissement(type);
            e.setStatut(statut);
        });
        //on formatte les nouveaux établissements en json pour sauvegarde
        etablissementDiviseEvent.setEtablisementsDivisesInBdd(objectMapper.writeValueAsString(etablissementDiviseEvent.getEtablissementDivises()));
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        eventService.save(etablissementDiviseEvent);
    }

    @DeleteMapping(value = "{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(@PathVariable String siren, @RequestBody MotifSuppressionWebDto motif) throws RestClientException {
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);

        EtablissementSupprimeEventEntity etablissementSupprimeEvent = new EtablissementSupprimeEventEntity(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        eventService.save(etablissementSupprimeEvent);

        //envoi du mail de suppression
        UserDetails user = userDetailsService.loadUser(etab);
        emailService.constructSuppressionMail(motif.getMotif(), ((UserDetailsImpl) user).getNameEtab(), ((UserDetailsImpl) user).getEmail());
    }

    @GetMapping(value = "/{siren}")
    public EtablissementWebDto get(@PathVariable String siren) throws InvalidTokenException {
        EtablissementEntity entity = etablissementService.getFirstBySiren(siren);
        UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUser(entity);
        if ("admin".equals(user.getRole())) {
            return mapper.map(entity, EtablissementAdminWebDto.class);
        }
        if (user.getSiren().equals(siren)) {
            return mapper.map(entity, EtablissementUserWebDto.class);
        }
        else {
            throw new InvalidTokenException("Le siren demandé ne correspond pas au siren de l'utilisateur connecté");
        }
    }

    @PostMapping(value = "/getDerniereDateModificationIp/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public String getDerniereDateModificationIp(@PathVariable String siren) throws DateException {
        log.info("debut getDerniereDateModificationIp");
        log.info("siren = " + siren);
        String res = "";

        try {
            ArrayList<String> listDateModifIp = new ArrayList<>();
            Set<IpEntity> listeIpsEtab = ipRepository.findAllBySiren(siren);
            for (IpEntity i : listeIpsEtab) {
                Date dateModif;
                if (i.getDateModification() != null) {
                    dateModif = i.getDateModification();
                    listDateModifIp.add(dateModif.toString());
                }
            }
            if (!listeIpsEtab.isEmpty() && listeIpsEtab.size() > 1) {
                ArrayList<String> listDateModifCourtes = new ArrayList<>();
                for (String date : listDateModifIp) listDateModifCourtes.add(date.substring(0, 10));
                log.info("dtaModif = " + listDateModifCourtes.get(0));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Collections.sort(listDateModifCourtes, Comparator.comparing(s -> LocalDate.parse(s, formatter).atStartOfDay()));
                log.info("dtaModif = " + listDateModifCourtes.get(0));
                log.info("dtaModif = " + listDateModifCourtes.get(listDateModifCourtes.size() - 1));
                res = listDateModifCourtes.get(listDateModifCourtes.size() - 1);
            }
            return res;
        } catch (Exception e) {
            throw new DateException("Erreur lors de la recupération de la dernière date de modification : " + e);
        }
    }

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('admin')")
    public List<EtablissementWebDto> getListEtab() {
        return mapper.mapList(etablissementService.findAll(), EtablissementWebDto.class);

    }

}
