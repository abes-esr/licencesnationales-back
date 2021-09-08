package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.entities.IpEntity;
import fr.abes.licencesnationales.core.event.etablissement.*;
import fr.abes.licencesnationales.core.exception.*;
import fr.abes.licencesnationales.core.repository.EventRepository;
import fr.abes.licencesnationales.core.repository.IpRepository;
import fr.abes.licencesnationales.core.services.ContactService;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.GenererIdAbes;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.exception.CaptchaException;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.exception.DonneeIncoherenteBddException;
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
@RequestMapping("/v1/ln/etablissement")
public class EtablissementController {

    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private ContactService contactService;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GenererIdAbes genererIdAbes;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    FiltrerAccesServices filtrerAccesServices;

    @Autowired
    EmailService emailService;

    @Value("${ln.dest.notif.admin}")
    private String admin;

    @PostMapping("/creationCompte")
    public void creationCompte(@Valid @RequestBody EtablissementCreeWebDto eventDTO) throws CaptchaException, SirenExistException, MailDoublonException, RestClientException {
        String captcha = eventDTO.getRecaptcha();
        String action = "creationCompte";

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, action);
        if(!reCaptchaResponse.isSuccess()){
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        //verifier que le siren n'est pas déjà en base
        boolean existeSiren = etablissementService.existeSiren(eventDTO.getSiren());
        log.info("existeSiren = "+ existeSiren);
        if (existeSiren) {
            throw new SirenExistException("Cet établissement existe déjà.");
        }
        //verifier que le mail du contact n'est pas déjà en base
        if (contactService.existeMail(eventDTO.getContact().getMail())) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        //on crypte le mot de passe + on génère un idAbes + on déclenche la méthode add du controlleur etab
        else{
            EtablissementCreeEvent etablissementCreeEvent = mapper.map(eventDTO, EtablissementCreeEvent.class);
            applicationEventPublisher.publishEvent(etablissementCreeEvent);
            eventRepository.save(new EventEntity(etablissementCreeEvent));
            emailService.constructCreationCompteEmailUser(eventDTO.getContact().getMail());
            emailService.constructCreationCompteEmailAdmin(admin, eventDTO.getSiren(), eventDTO.getName());
        }
    }


    @PostMapping(value = "/modification")
    public void edit(@Valid @RequestBody EtablissementModifieWebDto eventDTO) throws SirenIntrouvableException, AccesInterditException {
        log.info("debut EtablissementController modification");
        EtablissementModifieEvent etablissementModifieEvent =
                new EtablissementModifieEvent(this,
                        filtrerAccesServices.getSirenFromSecurityContextUser(),
                        eventDTO.getNomContact(),
                        eventDTO.getPrenomContact(),
                        eventDTO.getMailContact(),
                        eventDTO.getTelephoneContact(),
                        eventDTO.getAdresseContact(),
                        eventDTO.getBoitePostaleContact(),
                        eventDTO.getCodePostalContact(),
                        eventDTO.getVilleContact(),
                        eventDTO.getCedexContact());
        applicationEventPublisher.publishEvent(etablissementModifieEvent);
        eventRepository.save(new EventEntity(etablissementModifieEvent));
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public void fusion(@RequestBody EtablissementFusionneWebDto eventDTO) {
        EtablissementFusionneEvent etablissementFusionneEvent
                = new EtablissementFusionneEvent(this,
                eventDTO.getNom(),
                eventDTO.getSiren(),
                eventDTO.getTypeEtablissement(),
                eventDTO.getIdAbes(),
                eventDTO.getNomContact(),
                eventDTO.getPrenomContact(),
                eventDTO.getAdresseContact(),
                eventDTO.getBoitePostaleContact(),
                eventDTO.getCodePostalContact(),
                eventDTO.getVilleContact(),
                eventDTO.getCedexContact(),
                eventDTO.getTelephoneContact(),
                eventDTO.getMailContact(),
                eventDTO.getMotDePasse(),
                eventDTO.getRoleContact(),
                eventDTO.getSirenFusionnes());
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        eventRepository.save(new EventEntity(etablissementFusionneEvent));
    }

    @PostMapping(value = "/division")
    @PreAuthorize("hasAuthority('admin')")
    public void division(@RequestBody EtablissementDiviseWebDto eventDTO) {
        EtablissementDiviseEvent etablissementDiviseEvent = mapper.map(eventDTO, EtablissementDiviseEvent.class);
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        eventRepository.save(new EventEntity(etablissementDiviseEvent));
    }

    @DeleteMapping(value = "/suppression/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(HttpServletRequest request,  @PathVariable String siren, @RequestBody Map<String, String> motif) throws DonneeIncoherenteBddException, RestClientException {
        //envoi du mail de suppression
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        UserDetails user = new UserDetailsServiceImpl(etablissementService).loadUser(etab);
        String emailUser = ((UserDetailsImpl) user).getEmail();
        String nomEtab = ((UserDetailsImpl) user).getNameEtab();
        emailService.constructSuppressionMail(motif.get("motif"), nomEtab, emailUser);

        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        eventRepository.save(new EventEntity(etablissementSupprimeEvent));
    }

    @GetMapping(value = "/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public EtablissementWebDto get(@PathVariable String siren) {
        return mapper.map(etablissementService.getFirstBySiren(siren), EtablissementWebDto.class);
    }

    @PostMapping(value = "/getDerniereDateModificationIp/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public String getDerniereDateModificationIp(@PathVariable String siren) throws DateException {
        log.info("debut getDerniereDateModificationIp");
        log.info("siren = " + siren);
        String res = "";

        try{
            ArrayList<String> listDateModifIp = new ArrayList<>();
            Set<IpEntity> listeIpsEtab = ipRepository.findAllBySiren(siren);
            for(IpEntity i:listeIpsEtab) {
                Date dateModif;
                if(i.getDateModification()!=null){
                    dateModif=i.getDateModification();
                    listDateModifIp.add(dateModif.toString());
                }
            }
            if(!listeIpsEtab.isEmpty() && listeIpsEtab.size()>1) {
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
        } catch(Exception e) {
            throw new DateException("Erreur lors de la recupération de la dernière date de modification : " + e);
        }
    }

    @GetMapping(value = "/getInfoEtab")
    @PreAuthorize("hasAnyAuthority('etab', 'admin')")
    public EtablissementWebDto getInfoEtab() throws SirenIntrouvableException, AccesInterditException {
        return mapper.map(etablissementService.getFirstBySiren(filtrerAccesServices.getSirenFromSecurityContextUser()), EtablissementWebDto.class);
    }

    @GetMapping(value = "/getListEtab")
    @PreAuthorize("hasAuthority('admin')")
    public List<EtablissementWebDto> getListEtab() {
        return mapper.mapList(etablissementService.findAll(), EtablissementWebDto.class);

    }

}
