package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.*;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.exception.*;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.*;
import fr.abes.licencesnationales.core.services.export.ExportEtablissementAdmin;
import fr.abes.licencesnationales.core.services.export.ExportEtablissementUser;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementAdminWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementUserWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.TypeEtablissementDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/v1/etablissements")
public class EtablissementController extends AbstractController {

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
    private String admin;


    @PutMapping
    public ResponseEntity<Object> creationCompte(@Valid @RequestBody EtablissementCreeWebDto etablissementCreeWebDto, HttpServletRequest request) throws CaptchaException, RestClientException, JsonProcessingException, SirenExistException, MailDoublonException {
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        String captcha = etablissementCreeWebDto.getRecaptcha();

        if (captcha == null) {
            throw new CaptchaException(Constant.EXCEPTION_CAPTCHA_OBLIGATOIRE);
        }

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, "creationCompte");
        if (!reCaptchaResponse.isSuccess()) {
            throw new CaptchaException(Constant.ERROR_RECAPTCHA + reCaptchaResponse.getErrors());
        }
        if (etablissementService.existeSiren(etablissementCreeWebDto.getSiren())) {
            throw new SirenExistException(Constant.SIREN_DOUBLON);
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
        emailService.constructCreationCompteEmailUser(locale, etablissementCreeWebDto.getContact().getMail());
        emailService.constructCreationCompteEmailAdmin(locale, admin, etablissementCreeWebDto.getSiren(), etablissementCreeWebDto.getName());
        return buildResponseEntity(Constant.MESSAGE_CREATIONETAB_OK);
    }

    @PostMapping(value = "/{siren}")
    public ResponseEntity<Object> edit(@PathVariable String siren, @Valid @RequestBody EtablissementModifieWebDto etablissementModifieWebDto) throws SirenIntrouvableException, AccesInterditException, JsonProcessingException, MailDoublonException, InvalidTokenException {
        if (etablissementModifieWebDto instanceof EtablissementModifieUserWebDto) {
            if (filtrerAccesServices.getSirenFromSecurityContextUser().equals(siren)) {
                etablissementModifieWebDto.setSiren(siren);
            } else {
                throw new InvalidTokenException(Constant.SIREN_NE_CORRESPOND_PAS);
            }
        } else {
            if (!("admin").equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
                throw new AccesInterditException(Constant.OPERATION_QUE_PAR_ADMIN);
            }
        }
        EtablissementEntity etabInBdd = etablissementService.getFirstBySiren(etablissementModifieWebDto.getSiren());
        if (!(etabInBdd.getContact().getMail().equals(etablissementModifieWebDto.getContact().getMail())) && (etablissementService.existeMail(etablissementModifieWebDto.getContact().getMail()))) {
            throw new MailDoublonException(Constant.ERROR_DOUBLON_MAIL);
        }
        EtablissementModifieEventEntity event = mapper.map(etablissementModifieWebDto, EtablissementModifieEventEntity.class);
        event.setSource(this);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
        return buildResponseEntity(Constant.MESSAGE_MODIFETAB_OK);
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> fusion(@Valid @RequestBody EtablissementFusionneWebDto etablissementFusionneWebDto) throws JsonProcessingException {
        EtablissementFusionneEventEntity event = mapper.map(etablissementFusionneWebDto, EtablissementFusionneEventEntity.class);
        event.setSource(this);
        // On genère un identifiant Abes
        event.setIdAbes(GenererIdAbes.generateId());
        // On crypte le mot de passe
        event.setMotDePasse(passwordService.getEncodedMotDePasse(etablissementFusionneWebDto.getNouveauEtab().getContact().getMotDePasse()));
        event.setValide(true);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
        return buildResponseEntity(Constant.MESSAGE_FUSIONETAB_OK);

    }

    @PostMapping(value = "/scission")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> scission(@Valid @RequestBody EtablissementDiviseWebDto etablissementDiviseWebDto) throws UnknownTypeEtablissementException, JsonProcessingException, UnknownStatutException {
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
        return buildResponseEntity(Constant.MESSAGE_SCISSIONETAB_OK);
    }

    @DeleteMapping(value = "{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> suppression(@PathVariable String siren, HttpServletRequest request) throws RestClientException, JsonProcessingException {
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);

        EtablissementSupprimeEventEntity etablissementSupprimeEvent = new EtablissementSupprimeEventEntity(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        eventService.save(etablissementSupprimeEvent);

        //envoi du mail de suppression
        UserDetails user = userDetailsService.loadUser(etab);
        emailService.constructSuppressionCompteMailUser(locale, ((UserDetailsImpl) user).getNameEtab(), ((UserDetailsImpl) user).getEmail());
        emailService.constructSuppressionCompteMailAdmin(locale, admin, ((UserDetailsImpl) user).getNameEtab(), ((UserDetailsImpl) user).getSiren());
        return buildResponseEntity(Constant.MESSAGE_SUPPETAB_OK);
    }

    @PostMapping(value = "/validation/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> validation(@Valid @PathVariable String siren, HttpServletRequest request) throws JsonProcessingException, UnknownStatutException, BadStatutException {
        Locale locale = (request.getLocale().equals(Locale.FRANCE) ? Locale.FRANCE : Locale.ENGLISH);
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        if (etab.isValide()) {
            throw new BadStatutException(Constant.DEJA_VALIDE);
        }
        EtablissementValideEventEntity etablissementValideEvent= new EtablissementValideEventEntity(this, siren);
        etablissementValideEvent.setValide(true);
        applicationEventPublisher.publishEvent(etablissementValideEvent);
        eventService.save(etablissementValideEvent);

        //envoi du mail de validation
        UserDetails user = userDetailsService.loadUser(etab);
        emailService.constructValidationCompteMailUser(locale, ((UserDetailsImpl) user).getNameEtab(), ((UserDetailsImpl) user).getEmail());
        emailService.constructValidationCompteMailAdmin(locale, admin, ((UserDetailsImpl) user).getNameEtab(), ((UserDetailsImpl) user).getSiren());
        return buildResponseEntity(Constant.MESSAGE_VALIDATIONETAB_OK);
    }

    @GetMapping(value = "/{siren}")
    public EtablissementWebDto get(@PathVariable String siren) throws InvalidTokenException {
        EtablissementEntity entity = etablissementService.getFirstBySiren(siren);
        UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUser(entity);
        if ("admin".equals(filtrerAccesServices.getRoleFromSecurityContextUser())) {
            return mapper.map(entity, EtablissementAdminWebDto.class);
        }
        if (user.getSiren().equals(siren)) {
            return mapper.map(entity, EtablissementUserWebDto.class);
        } else {
            throw new InvalidTokenException(Constant.SIREN_NE_CORRESPOND_PAS);
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
            List<IpEntity> listeIpsEtab = ipRepository.findAllBySiren(siren);
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
            throw new DateException(Constant.ERROR_RECUP_DERNIERE_DATE_MODIF + e);
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

    @GetMapping(value = "/export")
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
        IOUtils.copy(stream , response.getOutputStream());
        response.flushBuffer();
    }

}
