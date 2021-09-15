package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.event.etablissement.*;
import fr.abes.licencesnationales.core.exception.*;
import fr.abes.licencesnationales.core.repository.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
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
    private EventService eventService;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    FiltrerAccesServices filtrerAccesServices;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GenererIdAbes genererIdAbes;

    @Value("${ln.dest.notif.admin}")
    private String admin;

    @PutMapping("/")
    public void creationCompte(@Valid @RequestBody EtablissementCreeWebDto etablissementCreeWebDto) throws CaptchaException, SirenExistException, MailDoublonException, RestClientException {
        String captcha = etablissementCreeWebDto.getRecaptcha();

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, "creationCompte");
        if(!reCaptchaResponse.isSuccess()){
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        // On convertit la DTO web (Json) en objet métier d'événement de création d'établissement
        EtablissementCreeEvent etablissementCreeEvent = mapper.map(etablissementCreeWebDto, EtablissementCreeEvent.class);

        // On genère un identifiant Abes
        etablissementCreeEvent.setIdAbes(GenererIdAbes.generateId());
        // On crypte le mot de passe
        etablissementCreeEvent.setMotDePasse(passwordEncoder.encode(etablissementCreeWebDto.getContact().getMotDePasse()));

        // On publie l'événement et on le sauvegarde
        applicationEventPublisher.publishEvent(etablissementCreeEvent);
        eventService.save((etablissementCreeEvent));


        /*******************************************/
        emailService.constructCreationCompteEmailUser(etablissementCreeWebDto.getContact().getMail());
        emailService.constructCreationCompteEmailAdmin(admin, etablissementCreeWebDto.getSiren(), etablissementCreeWebDto.getName());

    }


    @PostMapping(value = "/modification")
    public void edit(@Valid @RequestBody EtablissementModifieWebDto etablissementModifieWebDto) throws SirenIntrouvableException, AccesInterditException {
        log.info("debut EtablissementController modification");
        EtablissementModifieEvent etablissementModifieEvent =
                new EtablissementModifieEvent(this,
                        filtrerAccesServices.getSirenFromSecurityContextUser(),
                        etablissementModifieWebDto.getNomContact(),
                        etablissementModifieWebDto.getPrenomContact(),
                        etablissementModifieWebDto.getMailContact(),
                        etablissementModifieWebDto.getTelephoneContact(),
                        etablissementModifieWebDto.getAdresseContact(),
                        etablissementModifieWebDto.getBoitePostaleContact(),
                        etablissementModifieWebDto.getCodePostalContact(),
                        etablissementModifieWebDto.getVilleContact(),
                        etablissementModifieWebDto.getCedexContact());
        applicationEventPublisher.publishEvent(etablissementModifieEvent);
        eventService.save(etablissementModifieEvent);
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public void fusion(@RequestBody EtablissementFusionneWebDto etablissementFusionneWebDto) throws JsonProcessingException {
        EtablissementFusionneEvent etablissementFusionneEvent
                = new EtablissementFusionneEvent(this,
                etablissementFusionneWebDto.getNom(),
                etablissementFusionneWebDto.getSiren(),
                etablissementFusionneWebDto.getTypeEtablissement(),
                etablissementFusionneWebDto.getIdAbes(),
                etablissementFusionneWebDto.getNomContact(),
                etablissementFusionneWebDto.getPrenomContact(),
                etablissementFusionneWebDto.getAdresseContact(),
                etablissementFusionneWebDto.getBoitePostaleContact(),
                etablissementFusionneWebDto.getCodePostalContact(),
                etablissementFusionneWebDto.getVilleContact(),
                etablissementFusionneWebDto.getCedexContact(),
                etablissementFusionneWebDto.getTelephoneContact(),
                etablissementFusionneWebDto.getMailContact(),
                etablissementFusionneWebDto.getMotDePasse(),
                etablissementFusionneWebDto.getRoleContact(),
                etablissementFusionneWebDto.getSirenFusionnes());
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        //etablissementEventRepository.save(new EtablissementEventEntity(etablissementFusionneEvent));
    }

    @PostMapping(value = "/division")
    @PreAuthorize("hasAuthority('admin')")
    public void division(@RequestBody EtablissementDiviseWebDto etablissementDiviseWebDto) throws JsonProcessingException {
        EtablissementDiviseEvent etablissementDiviseEvent = mapper.map(etablissementDiviseWebDto, EtablissementDiviseEvent.class);
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
       // etablissementEventRepository.save(new EtablissementEventEntity(etablissementDiviseEvent));
    }

    @DeleteMapping(value = "/suppression/{siren}")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(@PathVariable String siren, @RequestBody Map<String, String> motif) throws DonneeIncoherenteBddException, RestClientException {
        //envoi du mail de suppression
        EtablissementEntity etab = etablissementService.getFirstBySiren(siren);
        UserDetails user = new UserDetailsServiceImpl(etablissementService).loadUser(etab);
        String emailUser = ((UserDetailsImpl) user).getEmail();
        String nomEtab = ((UserDetailsImpl) user).getNameEtab();
        emailService.constructSuppressionMail(motif.get("motif"), nomEtab, emailUser);

        EtablissementSupprimeEvent etablissementSupprimeEvent
                = new EtablissementSupprimeEvent(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        //etablissementEventRepository.save(new EtablissementEventEntity(etablissementSupprimeEvent));
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
