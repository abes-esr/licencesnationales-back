package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEventEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.repository.EditeurEventRepository;
import fr.abes.licencesnationales.core.repository.EditeurRepository;
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
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EditeurEventRepository eventRepository;

    @Autowired
    private UtilsMapper mapper;

    public void addEditeur(@Valid EditeurCreeEvent editeur) throws MailDoublonException, JsonProcessingException {

        log.info("debut addEditeur");
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeur(),editeur.getListeContactTechniqueEditeur());
        if (existeMail) {
            log.info("existeMail");
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else{
            log.info("addEditeur 1");
            log.info("editeurCreeEvent.get" + editeur.getNomEditeur());
            log.info("editeurCreeEvent.get" + editeur.getAdresseEditeur());
            log.info("editeurCreeEvent.get" + editeur.getDateCreation());
            Set<ContactCommercialEditeurEntity> s = editeur.getListeContactCommercialEditeur();
            for(ContactCommercialEditeurEntity c:s) {
                log.info("editeurCreeEvent.getListeContactCommercialEditeurDto() = " + c.getMailContact() + c.getPrenomContact() + c.getNomContact());
            }
            applicationEventPublisher.publishEvent(editeur);
            log.info("addEditeur 2");
            eventRepository.save(new EditeurEventEntity(editeur));
        }
    }

    public void updateEditeur(EditeurModifieEvent editeur) throws MailDoublonException, JsonProcessingException {
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeur(),editeur.getListeContactTechniqueEditeur());
        if (existeMail) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else {
            applicationEventPublisher.publishEvent(editeur);
            eventRepository.save(new EditeurEventEntity(editeur));
        }
    }

    public void fusionEditeur(EditeurFusionneEvent editeur) {
        //TODO : coder méthode fusion éditeur couche service
    }

    public EditeurEntity getFirstEditeurById(Long id) {
        return dao.getFirstByIdEditeur(id);
    }


    /*public Set<ContactTechniqueEditeurEntity> getAllCTByIdEditeur(EditeurEntity idEditeur) {
        return daoCT.getAllCTByEditeurEntity(idEditeur);
    }*/

    public List<EditeurEntity> findAllEditeur() {
        return dao.findAll();
    }

    public void deleteEditeur(String id) {
        EditeurSupprimeEvent editeurSupprimeEvent = new EditeurSupprimeEvent(this, Long.valueOf(id));
        applicationEventPublisher.publishEvent(editeurSupprimeEvent);
        eventRepository.save(new EditeurEventEntity(editeurSupprimeEvent));
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
