package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.core.dto.ip.IpDto;
import fr.abes.licencesnationales.core.entities.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.EditeurEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.web.dto.editeur.*;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.services.EditeurService;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


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
    public void creationEditeur(@Valid @RequestBody EditeurCreeWebDto editeurCreeWebDTO) throws MailDoublonException {
        EditeurCreeDto editeurCreeDto = mapper.map(editeurCreeWebDTO, EditeurCreeDto.class);
        editeurService.addEditeur(editeurCreeDto);
    }

    @PostMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void modificationEditeur (@PathVariable Long id, @RequestBody EditeurModifieWebDto editeurModifieDTO) throws MailDoublonException {
        EditeurModifieDto editeurModifieDto = mapper.map(editeurModifieDTO, EditeurModifieDto.class);
        editeurModifieDto.setId(id);
        editeurService.updateEditeur(editeurModifieDto);
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
            cce.nomContactCommercial = c.getNomContactCommercial();
            cce.prenomContactCommercial = c.getPrenomContactCommercial();
            cce.mailContactCommercial = c.getMailContactCommercial();
            CC.add(cce);
        }
        for (ContactTechniqueEditeurEntity t:ct) {
            ContactTechniqueEditeurWebDto cte = new ContactTechniqueEditeurWebDto();
            cte.nomContactTechnique = t.getNomContactTechnique();
            cte.prenomContactTechnique = t.getPrenomContactTechnique();
            cte.mailContactTechnique = t.getMailContactTechnique();
            CT.add(cte);
        }
        //editeurEntity.setContactCommercialEditeurEntities(editeurService.getAllCCByIdEditeur(id));
        //editeurEntity.setContactTechniqueEditeurEntities(editeurService.getAllCTByIdEditeur(editeurEntity));
        EditeurWebDto editeurWebDto = new EditeurWebDto();
        editeurWebDto.setNomEditeur(editeurEntity.getNomEditeur());
        editeurWebDto.setAdresseEditeur(editeurEntity.getAdresseEditeur());
        editeurWebDto.setIdentifiantEditeur(editeurEntity.getIdentifiantEditeur());
        editeurWebDto.setGroupesEtabRelies(editeurEntity.getGroupesEtabRelies());
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
    public void suppression(@PathVariable String id)  {
        log.info("id suppression = " + id);
        editeurService.deleteEditeur(id);
    }


}
