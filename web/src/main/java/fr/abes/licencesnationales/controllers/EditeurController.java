package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.dto.DtoMapper;
import fr.abes.licencesnationales.dto.editeur.EditeurCreeDTO;
import fr.abes.licencesnationales.dto.editeur.EditeurDTO;
import fr.abes.licencesnationales.dto.editeur.EditeurModifieDTO;
import fr.abes.licencesnationales.dto.editeur.EditeurSupprimeDTO;
import fr.abes.licencesnationales.entities.EventEntity;
import fr.abes.licencesnationales.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.exception.MailDoublonException;
import fr.abes.licencesnationales.repository.ContactCommercialEditeurRepository;
import fr.abes.licencesnationales.repository.ContactTechniqueEditeurRepository;
import fr.abes.licencesnationales.repository.EditeurRepository;
import fr.abes.licencesnationales.repository.EventRepository;
import fr.abes.licencesnationales.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/v1/ln/editeur")
public class EditeurController {
    @Autowired
    private DtoMapper mapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EditeurRepository editeurRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    ContactCommercialEditeurRepository contactCommercialEditeurRepository;

    @Autowired
    ContactTechniqueEditeurRepository contactTechniqueEditeurRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/creationEditeur")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity creationEditeur(@Valid @RequestBody EditeurCreeDTO editeurCreeDTO) throws MailDoublonException {
        log.info("DEBUT creationEditeur ");
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = emailService.checkDoublonMail(editeurCreeDTO.getListeContactCommercialEditeurDTO(),editeurCreeDTO.getListeContactTechniqueEditeurDTO());
        if (existeMail) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
        }
        else{
            EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeurCreeDTO);
            applicationEventPublisher.publishEvent(editeurCreeEvent);
            return eventRepository.save(new EventEntity(editeurCreeEvent));
        }
    }

    @PostMapping(value = "/modificationEditeur")
    @PreAuthorize("hasAuthority('admin')")
    public EventEntity modificationEditeur (@RequestBody EditeurModifieDTO editeurModifieDTO) throws MailDoublonException {
        //verifier que le mail du contact n'est pas déjà en base
        boolean existeMail = emailService.checkDoublonMail(editeurModifieDTO.getListeContactCommercialEditeurDTO(),editeurModifieDTO.getListeContactTechniqueEditeurDTO());
        if (existeMail) {
            throw new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
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
            return eventRepository.save(new EventEntity(editeurModifieEvent));
        }
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public EditeurDTO get(@PathVariable String id) {
        return mapper.map(editeurRepository.getFirstById(id), EditeurDTO.class);
    }


    @GetMapping(value = "/getListEditeurs")
    @PreAuthorize("hasAuthority('admin')")
    public List<EditeurDTO> getListEditeurs() {
        List<EditeurDTO> listeEditeurs = mapper.mapList(editeurRepository.findAll(), EditeurDTO.class);
        log.info(listeEditeurs.toString());
        return listeEditeurs;

    }

    @DeleteMapping(value = "/suppression")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(@Valid @RequestBody EditeurSupprimeDTO editeurSupprimeDTO)  {
        EditeurSupprimeEvent editeurSupprimeEvent = new EditeurSupprimeEvent(this,
                editeurSupprimeDTO.getId());
        applicationEventPublisher.publishEvent(editeurSupprimeEvent);
        eventRepository.save(new EventEntity(editeurSupprimeEvent));
    }
}
