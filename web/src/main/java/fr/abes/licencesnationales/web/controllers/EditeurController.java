package fr.abes.licencesnationales.web.controllers;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.web.dto.editeur.*;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.services.EditeurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


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
    public void creationEditeur(@Valid @RequestBody EditeurCreeDto editeurCreeDTO) throws MailDoublonException {
        log.info("creationEditeur début");
        //EditeurCreeDto editeurCreeDto = mapper.map(editeurCreeDTO, EditeurCreeDto.class);
        //log.info("editeurCreeDTO.getDateCreation() =  " + editeurCreeDTO.getDateCreation());
        editeurService.addEditeur(editeurCreeDTO);
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
        return mapper.map(editeurService.getFirstEditeurById(id), EditeurWebDto.class);
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
