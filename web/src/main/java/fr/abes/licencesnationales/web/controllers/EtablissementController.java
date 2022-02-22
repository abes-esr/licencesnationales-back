package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.NotificationAdminDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.*;
import fr.abes.licencesnationales.core.exception.*;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.*;
import fr.abes.licencesnationales.core.services.export.ExportEtablissementAdmin;
import fr.abes.licencesnationales.core.services.export.ExportEtablissementUser;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.fusion.EtablissementFusionneWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieUserWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.scission.EtablissementDiviseWebDto;
import fr.abes.licencesnationales.web.exception.CaptchaException;
import fr.abes.licencesnationales.web.exception.InvalidTokenException;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/v1/etablissements")
public class EtablissementController {

    @Autowired
    private UtilsMapper mapper;

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
    private EmailService emailService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ExportEtablissementUser exportEtablissementUser;

    @Autowired
    private ExportEtablissementAdmin exportEtablissementAdmin;

    @Value("${ln.dest.notif.admin}")
    private String mailAdmin;


    @PutMapping
    public void creationCompte(@Valid @RequestBody EtablissementCreeWebDto etablissementCreeWebDto, HttpServletRequest request) throws CaptchaException, RestClientException, JsonProcessingException, SirenExistException, MailDoublonException {
        String captcha = etablissementCreeWebDto.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException("Le champs 'recaptcha' est obligatoire");
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, "creationCompte");
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }
        if (etablissementService.existeSiren(etablissementCreeWebDto.getSiren())) {
            throw new SirenExistException("Le siren saisi est déjà utilisé");
        }
        if (etablissementService.existeMail(etablissementCreeWebDto.getContact().getMail())) {
            throw new MailDoublonException(Constant.ERROR_DOUBLON_MAIL);
        }
        // On convertit la DTO web (Json) en objet métier d'événement de création d'établissement
        EtablissementCreeEventEntity event = mapper.map(etablissementCreeWebDto, EtablissementCreeEventEntity.class);
        event.setSource(this);
        // On genère un identifiant Abes
        event.setIdAbes(GenererIdAbes.generateId());
        event.setValide(false);
        event.setMotDePasse(passwordService.getEncodedMotDePasse(etablissementCreeWebDto.getContact().getMotDePasse()));
        // On publie l'événement et on le sauvegarde
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);

        /*******************************************/
        emailService.constructCreationCompteEmailUser(event);
        emailService.constructCreationCompteEmailAdmin(mailAdmin, event.getNomEtab());
    }

    @PostMapping(value = "/{siren}")
    public void edit(@PathVariable String siren, @Valid @RequestBody EtablissementModifieWebDto etablissementModifieWebDto) throws SirenIntrouvableException, AccesInterditException, JsonProcessingException, MailDoublonException, InvalidTokenException {
        boolean envoiMail = false;
        if (etablissementModifieWebDto instanceof EtablissementModifieUserWebDto) {
            if (filtrerAccesServices.getSirenFromSecurityContextUser().equals(siren)) {
                etablissementModifieWebDto.setSiren(siren);
            } else {
                throw new InvalidTokenException("Le siren demandé ne correspond pas au siren de l'utilisateur connecté");
            }
        } else {
            if (!("admin").equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
                throw new AccesInterditException("L'opération ne peut être effectuée que par un administrateur");
            }
        }
        EtablissementEntity etabInBdd = etablissementService.getFirstBySiren(etablissementModifieWebDto.getSiren());
        String ancienMail = etabInBdd.getContact().getMail();
        if (!(ancienMail.equals(etablissementModifieWebDto.getContact().getMail()))) {
            if (etablissementService.existeMail(etablissementModifieWebDto.getContact().getMail())) {
                throw new MailDoublonException(Constant.ERROR_DOUBLON_MAIL);
            }
            envoiMail = true;
        }

        EtablissementModifieEventEntity event = mapper.map(etablissementModifieWebDto, EtablissementModifieEventEntity.class);
        event.setSource(this);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
        if (envoiMail) {
            emailService.constructModificationMailAdmin(mailAdmin, etabInBdd.getNom(), ancienMail, etablissementModifieWebDto.getContact().getMail());
        }
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public void fusion(@Valid @RequestBody EtablissementFusionneWebDto etablissementFusionneWebDto) throws JsonProcessingException {
        EtablissementFusionneEventEntity event = mapper.map(etablissementFusionneWebDto, EtablissementFusionneEventEntity.class);
        event.setSource(this);
        // On genère un identifiant Abes
        event.setIdAbes(GenererIdAbes.generateId());
        // On crypte le mot de passe
        event.setMotDePasse(passwordService.getEncodedMotDePasse(etablissementFusionneWebDto.getNouveauEtab().getContact().getMotDePasse()));
        event.setValide(true);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
    }

    @PostMapping(value = "/scission")
    @PreAuthorize("hasAuthority('admin')")
    public void scission(@Valid @RequestBody EtablissementDiviseWebDto etablissementDiviseWebDto) throws UnknownTypeEtablissementException, JsonProcessingException, UnknownStatutException {
        EtablissementDiviseEventEntity etablissementDiviseEvent = mapper.map(etablissementDiviseWebDto, EtablissementDiviseEventEntity.class);
        EtablissementEntity etablissement = etablissementService.getFirstBySiren(etablissementDiviseEvent.getAncienSiren());
        etablissementDiviseEvent.setTypeEtablissement(etablissement.getTypeEtablissement().getLibelle());
        etablissementDiviseEvent.setIdAbes(etablissement.getIdAbes());
        etablissementDiviseEvent.setAdresseContact(etablissement.getContact().getAdresse());
        etablissementDiviseEvent.setBoitePostaleContact(etablissement.getContact().getBoitePostale());
        etablissementDiviseEvent.setCedexContact(etablissement.getContact().getCedex());
        etablissementDiviseEvent.setCodePostalContact(etablissement.getContact().getCodePostal());
        etablissementDiviseEvent.setVilleContact(etablissement.getContact().getVille());
        etablissementDiviseEvent.setIdContact(etablissement.getContact().getId());
        etablissementDiviseEvent.setMailContact(etablissement.getContact().getMail());
        etablissementDiviseEvent.setTelephoneContact(etablissement.getContact().getTelephone());
        etablissementDiviseEvent.setPrenomContact(etablissement.getContact().getPrenom());
        etablissementDiviseEvent.setNomContact(etablissement.getContact().getNom());
        etablissementDiviseEvent.setMotDePasse(etablissement.getContact().getMotDePasse());
        etablissementDiviseEvent.setNomEtab(etablissement.getNom());
        etablissementDiviseEvent.setRoleContact(etablissement.getContact().getRole());
        etablissementDiviseEvent.setValide(etablissement.isValide());
        //on initialise le statut et le type des nouveaux établissement et on génère l'Id Abes ainsi que le mot de passe
        for (EtablissementEntity e : etablissementDiviseEvent.getEtablissementDivises()) {
            //on génère un identifiant Abes
            e.setIdAbes(GenererIdAbes.generateId());
            //on crypte le mode de passe
            e.getContact().setMotDePasse(passwordService.getEncodedMotDePasse(etablissementDiviseWebDto.getNouveauxEtabs().stream().filter(etab ->
                    etab.getSiren().equals(e.getSiren()))
                    .collect(Collectors.toList()).get(0).getContact().getMotDePasse()));
            e.setTypeEtablissement(referenceService.findTypeEtabByLibelle(etablissementDiviseWebDto.getNouveauxEtabs().stream().filter(etab ->
                    etab.getSiren().equals(e.getSiren()))
                    .collect(Collectors.toList()).get(0).getTypeEtablissement()));
            e.setValide(true);
        }
        //on formatte les nouveaux établissements en json pour sauvegarde
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        eventService.save(etablissementDiviseEvent);
    }

    @DeleteMapping(value = "{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(@PathVariable String siren) throws RestClientException, JsonProcessingException {
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);

        EtablissementSupprimeEventEntity event = new EtablissementSupprimeEventEntity(this, siren);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);

        //envoi du mail de suppression
        UserDetails user = userDetailsService.loadUser(etab);
        emailService.constructSuppressionCompteMailUserEtAdmin(etab.getNom(), ((UserDetailsImpl) user).getEmail(), mailAdmin);
    }

    @PostMapping(value = "/validation/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public void validation(@Valid @PathVariable String siren, HttpServletRequest request) throws JsonProcessingException, UnknownStatutException, BadStatutException {
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        if (etab.isValide()) {
            throw new BadStatutException("L'établissement ne doit pas déjà être validé");
        }
        EtablissementValideEventEntity etablissementValideEvent = new EtablissementValideEventEntity(this, siren);
        etablissementValideEvent.setValide(true);
        applicationEventPublisher.publishEvent(etablissementValideEvent);
        eventService.save(etablissementValideEvent);

        //envoi du mail de validation
        UserDetails user = userDetailsService.loadUser(etab);
        emailService.constructValidationCompteMailUser(((UserDetailsImpl) user).getNameEtab(), ((UserDetailsImpl) user).getEmail());
        emailService.constructValidationCompteMailAdmin(mailAdmin, ((UserDetailsImpl) user).getNameEtab());

    }

    @GetMapping(value = "/{siren}")
    public EtablissementWebDto get(@PathVariable String siren) throws InvalidTokenException, SirenIntrouvableException, AccesInterditException {
        EtablissementEntity entity = etablissementService.getFirstBySiren(siren);
        if ("admin".equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
            return mapper.map(entity, EtablissementAdminWebDto.class);
        }
        if (filtrerAccesServices.getSirenFromSecurityContextUser().equals(siren)) {
            return mapper.map(entity, EtablissementUserWebDto.class);
        } else {
            throw new InvalidTokenException("Le siren demandé ne correspond pas au siren de l'utilisateur connecté");
        }
    }

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('admin')")
    public List<EtablissementAdminWebDto> getListEtab() {
        return mapper.mapList(etablissementService.findAll(), EtablissementAdminWebDto.class);

    }

    @GetMapping(value = "/getType")
    public List<TypeEtablissementDto> getListType() {
        return mapper.mapList(referenceService.findAllTypeEtab(), TypeEtablissementDto.class);
    }

    @PostMapping(value = "/export")
    public void exportEtab(@RequestBody List<String> sirens, HttpServletResponse response) throws IOException, SirenIntrouvableException, AccesInterditException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=\"export.csv\"");
        InputStream stream;
        if ("admin".equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
            stream = exportEtablissementAdmin.generateCsv(sirens);
        } else {
            List liste = new ArrayList();
            liste.add(filtrerAccesServices.getSirenFromSecurityContextUser());
            stream = exportEtablissementUser.generateCsv(liste);
        }
        IOUtils.copy(stream, response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping(value = "/notifications/{siren}")
    public NotificationsDto getNotificationsEtab(@PathVariable String siren) throws SirenIntrouvableException, AccesInterditException, InvalidTokenException {
        if (filtrerAccesServices.getSirenFromSecurityContextUser().equals(siren)) {
            EtablissementEntity entity = etablissementService.getFirstBySiren(siren);
            return mapper.map(entity, NotificationsDto.class);
        } else {
            throw new InvalidTokenException("Le siren demandé ne correspond pas au siren de l'utilisateur connecté");
        }
    }

    @GetMapping(value = "/notificationsAdmin")
    @PreAuthorize("hasAuthority('admin')")
    public NotificationsAdminDto getNotificationsAdmin() {
        NotificationsAdminDto dtos = new NotificationsAdminDto();
        List<EtablissementEntity> listEtab = etablissementService.findAll();
        dtos.ajouterListNotif(etablissementService.getEtabNonValides(listEtab));
        dtos.ajouterListNotif(etablissementService.getEtabIpEnValidation(listEtab));
        dtos.ajouterListNotif(etablissementService.getEtabIpSupprimee(listEtab));
        dtos.getNotifications().stream().sorted((n1, n2) -> n2.getDateEvent().compareTo(n1.getDateEvent()));
        return dtos;
    }



}
