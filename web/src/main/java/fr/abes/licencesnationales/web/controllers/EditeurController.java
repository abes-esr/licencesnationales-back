package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.core.dto.ip.IpDto;
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
        log.info("creationEditeur début");
        log.info("editeurCreeDTO.getDateCreation() =  " + editeurCreeWebDTO.getDateCreation());
        log.info("editeurCreeDTO.getNomEditeur() =  " + editeurCreeWebDTO.getNomEditeur());
        log.info("editeurCreeDTO.getAdresseCreation() =  " + editeurCreeWebDTO.getAdresseEditeur());
        log.info("editeurCreeDTO.getGroupesEtabRelies() =  " + editeurCreeWebDTO.getGroupesEtabRelies());
        Set<ContactCommercialEditeurWebDto> cc = editeurCreeWebDTO.getListeContactCommercialEditeurWebDto();
        Set<ContactTechniqueEditeurWebDto> ct = editeurCreeWebDTO.getListeContactTechniqueEditeurWebDto();
        for (ContactCommercialEditeurWebDto c:cc)
            log.info(" ListeContactCommercialEditeurDto =  " + c.mailContactCommercial + c.nomContactCommercial + c.prenomContactCommercial);
        for (ContactTechniqueEditeurWebDto t:ct)
            log.info(" ListeContactCommercialEditeurDto =  " + t.mailContactTechnique + t.nomContactTechnique + t.prenomContactTechnique);

        //EditeurCreeDto editeurCreeDto = mapper.map(editeurCreeWebDTO, EditeurCreeDto.class);
        //log.info("editeurCreeDTO.getDateCreation() =  " + editeurCreeDto.getDateCreation());
        EditeurCreeDto editeurCreeDto = new EditeurCreeDto();
        editeurCreeDto.setNomEditeur(editeurCreeWebDTO.getNomEditeur());
        editeurCreeDto.setIdentifiantEditeur(editeurCreeWebDTO.getIdentifiantEditeur());
        editeurCreeDto.setGroupesEtabRelies(editeurCreeWebDTO.getGroupesEtabRelies());
        editeurCreeDto.setAdresseEditeur(editeurCreeWebDTO.getAdresseEditeur());
        editeurCreeDto.setDateCreation(editeurCreeWebDTO.getDateCreation());


        Set<ContactCommercialEditeurDto> CC = new HashSet<>();
        Set<ContactTechniqueEditeurDto> CT = new HashSet<>();
        for (ContactCommercialEditeurWebDto c:cc) {
            ContactCommercialEditeurDto cce = new ContactCommercialEditeurDto();
            cce.nomContactCommercial = c.nomContactCommercial;
            cce.prenomContactCommercial = c.prenomContactCommercial;
            cce.mailContactCommercial = c.mailContactCommercial;
            CC.add(cce);
        }
        for (ContactTechniqueEditeurWebDto t:ct) {
            ContactTechniqueEditeurDto cte = new ContactTechniqueEditeurDto();
            cte.nomContactTechnique = t.nomContactTechnique;
            cte.prenomContactTechnique = t.prenomContactTechnique;
            cte.mailContactTechnique = t.mailContactTechnique;
            CT.add(cte);
        }
        for (ContactCommercialEditeurDto C:CC)
            log.info(" ListeContactCommercialEditeurDto après remplissage =  " + C.mailContactCommercial + C.nomContactCommercial + C.prenomContactCommercial);
        for (ContactTechniqueEditeurDto T:CT)
            log.info(" ListeContactCommercialEditeurDto après remplissage =  " + T.mailContactTechnique + T.nomContactTechnique + T.prenomContactTechnique);

        editeurCreeDto.setListeContactCommercialEditeurDto(CC);
        editeurCreeDto.setListeContactTechniqueEditeurDto(CT);
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
        editeurEntity.setContactCommercialEditeurEntities(editeurService.getAllCCByIdEditeur(String.valueOf(id)));
        editeurEntity.setContactTechniqueEditeurEntities(editeurService.getAllCTByIdEditeur(String.valueOf(id)));
        return mapper.map(editeurEntity, EditeurWebDto.class);
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
