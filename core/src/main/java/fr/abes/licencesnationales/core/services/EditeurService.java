package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurSupprimeEventEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.repository.editeur.EditeurEventRepository;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class EditeurService {
    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EditeurEventRepository eventRepository;


    public void save(@Valid EditeurEntity editeur) throws MailDoublonException {

        log.debug("debut addEditeur");
        boolean existeMail = etablissementService.checkDoublonMail(editeur.getContactCommercialEditeurEntities(), editeur.getContactTechniqueEditeurEntities());
        if (existeMail) {
            log.debug("existeMail");
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        } else {
            editeurRepository.save(editeur);
        }

        if (editeur.getIdEditeur() == null) {
            // Création
        } else {
            // Modification
        }
    }

    public EditeurEntity getFirstEditeurById(Long id) {
        return editeurRepository.getFirstByIdEditeur(id);
    }

    public List<EditeurEntity> findAllEditeur() {
        return editeurRepository.findAll();
    }

    public void deleteEditeur(String id) throws JsonProcessingException {
        EditeurSupprimeEventEntity editeurSupprimeEvent = new EditeurSupprimeEventEntity(this, Integer.parseInt(id));
        applicationEventPublisher.publishEvent(editeurSupprimeEvent);
        eventRepository.save(editeurSupprimeEvent);
    }

    public void deleteById(Integer id) {
        editeurRepository.deleteById(id);
    }
}
