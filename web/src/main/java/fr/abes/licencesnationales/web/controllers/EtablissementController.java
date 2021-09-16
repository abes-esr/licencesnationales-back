package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.*;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.exception.*;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.ContactService;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

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
    private EtablissementEventRepository etablissementEventRepository;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private ContactService contactService;

    @Autowired
    private  ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    FiltrerAccesServices filtrerAccesServices;

    @Autowired
    EmailService emailService;

    @Value("${ln.dest.notif.admin}")
    private String admin;

    @PostMapping("/creationCompte")
    public void creationCompte(@Valid @RequestBody EtablissementCreeWebDto etablissementCreeWebDto) throws CaptchaException, SirenExistException, MailDoublonException, RestClientException {
        String captcha = etablissementCreeWebDto.getRecaptcha();
        String action = "creationCompte";

        //verifier la réponse fr.abes.licencesnationales.web.recaptcha
        ReCaptchaResponse reCaptchaResponse = reCaptchaService.verify(captcha, action);
        if(!reCaptchaResponse.isSuccess()){
            throw new CaptchaException("Erreur Recaptcha : " + reCaptchaResponse.getErrors());
        }

        //verifier que le siren n'est pas déjà en base
        boolean existeSiren = etablissementService.existeSiren(etablissementCreeWebDto.getSiren());
        log.info("existeSiren = "+ existeSiren);
        if (existeSiren) {
            throw new SirenExistException("Cet établissement existe déjà.");
        }
        //verifier que le mail du contact n'est pas déjà en base
        if (contactService.existeMail(etablissementCreeWebDto.getContact().getMail())) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        //on crypte le mot de passe + on génère un idAbes + on déclenche la méthode add du controlleur etab
        else{
            EtablissementCreeEventEntity etablissementCreeEvent = mapper.map(etablissementCreeWebDto, EtablissementCreeEventEntity.class);
            etablissementCreeEvent.setSource(this);
            applicationEventPublisher.publishEvent(etablissementCreeEvent);
            etablissementEventRepository.save(etablissementCreeEvent);
            emailService.constructCreationCompteEmailUser(etablissementCreeWebDto.getContact().getMail());
            emailService.constructCreationCompteEmailAdmin(admin, etablissementCreeWebDto.getSiren(), etablissementCreeWebDto.getName());
        }
    }


    @PostMapping(value = "/modification")
    public void edit(@Valid @RequestBody EtablissementModifieWebDto etablissementModifieWebDto) throws SirenIntrouvableException, AccesInterditException {
        log.info("debut EtablissementController modification");
        String siren = filtrerAccesServices.getSirenFromSecurityContextUser();
        EtablissementEntity etablissement = etablissementService.getFirstBySiren(siren);
        EtablissementModifieEventEntity etablissementModifieEvent = mapper.map(etablissementModifieWebDto, EtablissementModifieEventEntity.class);
        //on initialise les champs non modifiable à la valeur originale trouvée dans la base
        etablissementModifieEvent.setNomEtab(etablissement.getName());
        etablissementModifieEvent.setTypeEtablissement(etablissement.getTypeEtablissement().getLibelle());
        etablissementModifieEvent.setSiren(etablissement.getSiren());
        etablissementModifieEvent.setMotDePasse(etablissement.getContact().getMotDePasse());
        etablissementModifieEvent.setRoleContact(etablissement.getContact().getRole());

        applicationEventPublisher.publishEvent(etablissementModifieEvent);
        etablissementEventRepository.save(etablissementModifieEvent);
    }

    @PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public void fusion(@RequestBody EtablissementFusionneWebDto etablissementFusionneWebDto) {
        EtablissementFusionneEventEntity etablissementFusionneEvent = mapper.map(etablissementFusionneWebDto, EtablissementFusionneEventEntity.class);
        applicationEventPublisher.publishEvent(etablissementFusionneEvent);
        etablissementEventRepository.save(etablissementFusionneEvent);
    }

    @PostMapping(value = "/division")
    @PreAuthorize("hasAuthority('admin')")
    public void division(@RequestBody EtablissementDiviseWebDto etablissementDiviseWebDto) {
        EtablissementDiviseEventEntity etablissementDiviseEvent = mapper.map(etablissementDiviseWebDto, EtablissementDiviseEventEntity.class);
        applicationEventPublisher.publishEvent(etablissementDiviseEvent);
        etablissementEventRepository.save(etablissementDiviseEvent);
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

        EtablissementSupprimeEventEntity etablissementSupprimeEvent = new EtablissementSupprimeEventEntity(this, siren);
        applicationEventPublisher.publishEvent(etablissementSupprimeEvent);
        etablissementEventRepository.save(etablissementSupprimeEvent);
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
