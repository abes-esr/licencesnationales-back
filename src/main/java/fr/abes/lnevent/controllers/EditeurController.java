package fr.abes.lnevent.controllers;

import fr.abes.lnevent.dto.editeur.*;
import fr.abes.lnevent.dto.etablissement.EtablissementCreeDTO;
import fr.abes.lnevent.dto.etablissement.EtablissementModifieDTO;
import fr.abes.lnevent.dto.ip.IpModifieeDTO;
import fr.abes.lnevent.dto.ip.IpSupprimeeDTO;
import fr.abes.lnevent.entities.*;
import fr.abes.lnevent.event.editeur.EditeurCreeEvent;
import fr.abes.lnevent.event.editeur.EditeurFusionneEvent;
import fr.abes.lnevent.event.editeur.EditeurModifieEvent;
import fr.abes.lnevent.event.editeur.EditeurSupprimeEvent;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
import fr.abes.lnevent.event.etablissement.EtablissementModifieEvent;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.exception.AccesInterditException;
import fr.abes.lnevent.exception.SirenIntrouvableException;
import fr.abes.lnevent.recaptcha.ReCaptchaResponse;
import fr.abes.lnevent.repository.EditeurRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.EventRepository;
import fr.abes.lnevent.services.GenererIdAbes;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/v1/ln/editeur")
public class EditeurController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private String demandeOk = "Votre demande a été prise en compte.";

   /* @PostMapping(value = "/creation")
    @PreAuthorize("hasAuthority('admin')")
    public String add(@RequestBody EditeurCreeDTO editeurCreeDTO) {
        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this,
                editeurCreeDTO.getNomEditeur(),
                editeurCreeDTO.getIdentifiantEditeur(),
                editeurCreeDTO.getAdresseEditeur(),
                editeurCreeDTO.getContactCommercialEditeurDTO(),
                editeurCreeDTO.getContactTechniqueEditeurDTO());
        applicationEventPublisher.publishEvent(editeurCreeEvent);
        eventRepository.save(new EventEntity(editeurCreeEvent));
        return "done";
    }*/

    @PostMapping("/creationEditeur")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> creationEditeur(HttpServletRequest request, @Valid @RequestBody EditeurCreeDTO editeurCreeDTO) {



        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = checkDoublonMail(editeurCreeDTO.getListeContactCommercialEditeurDTO(),editeurCreeDTO.getListeContactTechniqueEditeurDTO());

        if (existeMail) {
            return ResponseEntity
                    .badRequest()
                    .body("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else{
            EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this,
                    editeurCreeDTO);
            applicationEventPublisher.publishEvent(editeurCreeEvent);
            eventRepository.save(new EventEntity(editeurCreeEvent));
            return ResponseEntity.ok(demandeOk);
        }
    }

    public boolean checkDoublonMail(Set<ContactCommercialEditeurDTO> c, Set<ContactTechniqueEditeurDTO> t) {
        log.info("DEBUT checkDoublonMail ");
        boolean existeMailCommercial = false;
        boolean existeMailTechnique = false;
        String mail = "";
        for (ContactCommercialEditeurDTO contact : c){
            mail = contact.getMailContactCommercial();
            existeMailCommercial = editeurRepository.findEditeurEntityByContactCommercialEditeurEntitiesContains(mail);
        }
        for (ContactCommercialEditeurDTO contact : c){
            mail = contact.getMailContactCommercial();
            existeMailTechnique = editeurRepository.findEditeurEntityByContactTechniqueEditeurEntitiesContains(mail);
        }
        log.info("existeMailCommercial = "+ existeMailCommercial);
        log.info("existeMailTechnique = "+ existeMailTechnique);
        if (existeMailCommercial || existeMailTechnique) {
            return true;
        }
        else return false;
    }

    @PostMapping(value = "/modificationEditeur")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> modificationEditeur (@RequestBody EditeurModifieDTO editeurModifieDTO) {
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = checkDoublonMail(editeurModifieDTO.getListeContactCommercialEditeurDTO(),editeurModifieDTO.getListeContactTechniqueEditeurDTO());
        if (existeMail) {
            return ResponseEntity
                    .badRequest()
                    .body("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else {
            EditeurModifieEvent editeurModifieEvent = new EditeurModifieEvent(this,
                    editeurModifieDTO.getNomEditeur(),
                    editeurModifieDTO.getIdentifiantEditeur(),
                    editeurModifieDTO.getGroupesEtabRelies(),
                    editeurModifieDTO.getAdresseEditeur(),
                    editeurModifieDTO.getListeContactCommercialEditeurDTO(),
                    editeurModifieDTO.getListeContactTechniqueEditeurDTO());
            applicationEventPublisher.publishEvent(editeurModifieEvent);
            eventRepository.save(new EventEntity(editeurModifieEvent));
            return ResponseEntity.ok(demandeOk);
        }
    }

    /*@PostMapping(value = "/fusion")
    @PreAuthorize("hasAuthority('admin')")
    public String fusion(@RequestBody EditeurFusionneDTO editeurFusionneDTO) {
        EditeurFusionneEvent editeurFusionneEvent = new EditeurFusionneEvent(this,
                editeurFusionneDTO.getEditeurDTO(),
                editeurFusionneDTO.getIdEditeurFusionnes());
        applicationEventPublisher.publishEvent(editeurFusionneEvent);
        eventRepository.save(new EventEntity(editeurFusionneEvent));
        return "done";
    }*/


    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public EditeurEntity get(@PathVariable String id) {
        return editeurRepository.getFirstById(id);
    }


    @GetMapping(value = "/getListEditeurs")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> getListEditeur() throws JSONException {

        List<JSONObject> listeEditeurs = new ArrayList<JSONObject>();
        List<EditeurEntity> liste = editeurRepository.findAll();
        for(EditeurEntity e : liste) {
            JSONObject editeurs = new JSONObject();
            editeurs.put("id", e.getId());
            editeurs.put("nomEditeur", e.getNomEditeur());
            listeEditeurs.add(editeurs);
        }
        log.info(listeEditeurs.toString());
        return new ResponseEntity<>(listeEditeurs.toString(), HttpStatus.OK);

    }
    @PostMapping(value = "/suppression")
    @PreAuthorize("hasAuthority('admin')")
    public String suppression(@Valid @RequestBody EditeurSupprimeDTO editeurSupprimeDTO)  {
        EditeurSupprimeEvent editeurSupprimeEvent = new EditeurSupprimeEvent(this,
                editeurSupprimeDTO.getId()/*, editeurSupprimeDTO.getSiren()*/);
        applicationEventPublisher.publishEvent(editeurSupprimeEvent);
        eventRepository.save(new EventEntity(editeurSupprimeEvent));
        return demandeOk;
    }
}
