package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.dto.editeur.*;
import fr.abes.licencesnationales.core.entities.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.EditeurEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.repository.ContactCommercialEditeurRepository;
import fr.abes.licencesnationales.core.repository.ContactTechniqueEditeurRepository;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
import fr.abes.licencesnationales.core.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class EditeurService {
    @Autowired
    private EditeurRepository dao;

    @Autowired
    ContactCommercialEditeurRepository daoCC;

    @Autowired
    ContactTechniqueEditeurRepository daoCT;



    @Autowired EmailService emailService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EventRepository eventRepository;

    public void addEditeur(@Valid EditeurCreeDto editeur) throws MailDoublonException {

        log.info("debut addEditeur");
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeurDto(),editeur.getListeContactTechniqueEditeurDto());
        if (existeMail) {
            log.info("existeMail");
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else{
            EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeur);
            log.info("addEditeur 1");
            log.info("editeurCreeEvent.get" + editeurCreeEvent.getEditeur().getNomEditeur());
            log.info("editeurCreeEvent.get" + editeurCreeEvent.getEditeur().getAdresseEditeur());
            log.info("editeurCreeEvent.get" + editeurCreeEvent.getEditeur().getDateCreation());
            Set<ContactCommercialEditeurDto> s = editeurCreeEvent.getEditeur().getListeContactCommercialEditeurDto();
            for(ContactCommercialEditeurDto c:s) {
                log.info("editeurCreeEvent.getListeContactCommercialEditeurDto() = " + c.mailContactCommercial + c.prenomContactCommercial + c.nomContactCommercial);
            }
            applicationEventPublisher.publishEvent(editeurCreeEvent);
            log.info("addEditeur 2");
            eventRepository.save(new EventEntity(editeurCreeEvent));
        }
    }

    public void updateEditeur(EditeurModifieDto editeur) throws MailDoublonException {
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeurDto(),editeur.getListeContactTechniqueEditeurDto());
        if (existeMail) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else {
            EditeurModifieEvent editeurModifieEvent = new EditeurModifieEvent(this, editeur);
            applicationEventPublisher.publishEvent(editeurModifieEvent);
            eventRepository.save(new EventEntity(editeurModifieEvent));
        }
    }

    public void fusionEditeur(EditeurFusionneDto editeur) {
        //TODO : coder méthode fusion éditeur couche service
    }

    public EditeurEntity getFirstEditeurById(Long id) {
        return dao.getFirstByIdEditeur(id);
    }

    public Set<ContactCommercialEditeurEntity> getAllCCByIdEditeur(String idEditeur) {
        return daoCC.getAllCCByIdEditeur(idEditeur);
    }

    public Set<ContactTechniqueEditeurEntity> getAllCTByIdEditeur(String idEditeur) {
        return daoCT.getAllCTByIdEditeur(idEditeur);
    }

    public List<EditeurEntity> findAllEditeur() {
        return dao.findAll();
    }

    public void deleteEditeur(String id) {
        EditeurSupprimeEvent editeurSupprimeEvent = new EditeurSupprimeEvent(this, Long.valueOf(id));
        applicationEventPublisher.publishEvent(editeurSupprimeEvent);
        eventRepository.save(new EventEntity(editeurSupprimeEvent));
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
