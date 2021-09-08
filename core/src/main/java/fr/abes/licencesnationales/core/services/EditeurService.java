package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.contact.*;
import fr.abes.licencesnationales.core.entities.EditeurEntity;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
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
    private ContactCommercialEditeurRepository daoCC;

    @Autowired
    private ContactTechniqueEditeurRepository daoCT;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UtilsMapper mapper;

    public void addEditeur(@Valid EditeurCreeEvent editeur) throws MailDoublonException {

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
            Set<ContactCommercialEditeurDto> s = editeur.getListeContactCommercialEditeur();
            for(ContactCommercialEditeurDto c:s) {
                log.info("editeurCreeEvent.getListeContactCommercialEditeurDto() = " + c.getMailContactCommercial() + c.getPrenomContactCommercial() + c.getNomContactCommercial());
            }
            applicationEventPublisher.publishEvent(editeur);
            log.info("addEditeur 2");
            eventRepository.save(new EventEntity(editeur));
        }
    }

    public void updateEditeur(EditeurModifieEvent editeur) throws MailDoublonException {
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeur(),editeur.getListeContactTechniqueEditeur());
        if (existeMail) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else {
            applicationEventPublisher.publishEvent(editeur);
            eventRepository.save(new EventEntity(editeur));
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
        eventRepository.save(new EventEntity(editeurSupprimeEvent));
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
