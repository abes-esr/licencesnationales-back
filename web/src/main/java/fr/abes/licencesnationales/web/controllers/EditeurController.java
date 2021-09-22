package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.services.EditeurService;
import fr.abes.licencesnationales.web.dto.editeur.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/v1/ln/editeur")
public class EditeurController {
    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private EditeurService editeurService;


    @PutMapping("/")
    @PreAuthorize("hasAuthority('admin')")
    public void creationEditeur(@Valid @RequestBody EditeurCreeWebDto editeurCreeWebDTO) throws MailDoublonException, JsonProcessingException {
        EditeurCreeEventEntity editeurCreeEvent = mapper.map(editeurCreeWebDTO, EditeurCreeEventEntity.class);
    }

    @PostMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void modificationEditeur (@PathVariable Long id, @RequestBody EditeurModifieWebDto editeurModifieDTO) throws MailDoublonException, JsonProcessingException {
        EditeurModifieEventEntity editeurModifieEvent = mapper.map(editeurModifieDTO, EditeurModifieEventEntity.class);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public EditeurWebDto get(@PathVariable Long id) {
        EditeurEntity editeurEntity   = editeurService.getFirstEditeurById(id);
        Set<ContactCommercialEditeurEntity> cc = editeurEntity.getContactCommercialEditeurEntities();
        Set<ContactTechniqueEditeurEntity> ct = editeurEntity.getContactTechniqueEditeurEntities();
        Set<ContactCommercialEditeurWebDto> CC = new HashSet<>();
        Set<ContactTechniqueEditeurWebDto> CT = new HashSet<>();
        for (ContactCommercialEditeurEntity c:cc) {
            ContactCommercialEditeurWebDto cce = new ContactCommercialEditeurWebDto();
            cce.nomContactCommercial = c.getNomContact();
            cce.prenomContactCommercial = c.getPrenomContact();
            cce.mailContactCommercial = c.getMailContact();
            CC.add(cce);
        }
        for (ContactTechniqueEditeurEntity t:ct) {
            ContactTechniqueEditeurWebDto cte = new ContactTechniqueEditeurWebDto();
            cte.nomContactTechnique = t.getNomContact();
            cte.prenomContactTechnique = t.getPrenomContact();
            cte.mailContactTechnique = t.getMailContact();
            CT.add(cte);
        }
        //editeurEntity.setContactCommercialEditeurEntities(editeurService.getAllCCByIdEditeur(id));
        //editeurEntity.setContactTechniqueEditeurEntities(editeurService.getAllCTByIdEditeur(editeurEntity));
        EditeurWebDto editeurWebDto = new EditeurWebDto();
        editeurWebDto.setNomEditeur(editeurEntity.getNomEditeur());
        editeurWebDto.setAdresseEditeur(editeurEntity.getAdresseEditeur());
        editeurWebDto.setIdentifiantEditeur(editeurEntity.getIdentifiantEditeur());
        editeurWebDto.setListeContactCommercialEditeurWebDto(CC);
        editeurWebDto.setListeContactTechniqueEditeurWebDto(CT);
        return editeurWebDto;
        //return mapper.map(editeurEntity, EditeurWebDto.class);
    }


    @GetMapping(value = "/getListEditeurs")
    @PreAuthorize("hasAuthority('admin')")
    public List<EditeurWebDto> getListEditeurs() {
        return mapper.mapList(editeurService.findAllEditeur(), EditeurWebDto.class);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(@PathVariable String id) throws JsonProcessingException {
        log.info("id suppression = " + id);
        editeurService.deleteEditeur(id);
    }


}
