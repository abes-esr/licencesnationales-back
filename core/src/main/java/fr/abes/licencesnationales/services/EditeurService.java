package fr.abes.licencesnationales.services;

import fr.abes.licencesnationales.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.dto.editeur.EditeurFusionneDto;
import fr.abes.licencesnationales.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.entities.EditeurEntity;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.exception.MailDoublonException;
import fr.abes.licencesnationales.repository.EditeurRepository;
import fr.abes.licencesnationales.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditeurService {
    @Autowired
    private EditeurRepository dao;

    @Autowired EmailService emailService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EventRepository eventRepository;

    public void addEditeur(EditeurCreeDto editeur) throws MailDoublonException {
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeurEventDto(),editeur.getListeContactTechniqueEditeurEventDto());
        if (existeMail) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else{
            EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeur);
            applicationEventPublisher.publishEvent(editeurCreeEvent);
            eventRepository.save(new EventEntity(editeurCreeEvent));
        }
    }

    public void updateEditeur(EditeurModifieDto editeur) throws MailDoublonException {
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = emailService.checkDoublonMail(editeur.getListeContactCommercialEditeurEventDto(),editeur.getListeContactTechniqueEditeurEventDto());
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
        return dao.getFirstById(id);
    }

    public List<EditeurEntity> findAllEditeur() {
        return dao.findAll();
    }

    public void deleteEditeur(String id) {
        EditeurSupprimeEvent editeurSupprimeEvent = new EditeurSupprimeEvent(this, id);
        applicationEventPublisher.publishEvent(editeurSupprimeEvent);
        eventRepository.save(new EventEntity(editeurSupprimeEvent));
    }
}
