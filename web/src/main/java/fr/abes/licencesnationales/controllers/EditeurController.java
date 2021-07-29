package fr.abes.licencesnationales.controllers;


import fr.abes.licencesnationales.converter.UtilsMapper;
import fr.abes.licencesnationales.dto.editeur.*;
import fr.abes.licencesnationales.exception.MailDoublonException;
import fr.abes.licencesnationales.services.EditeurService;
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
    public void creationEditeur(@Valid @RequestBody EditeurCreeWebDto editeurCreeDTO) throws MailDoublonException {
        EditeurCreeDto editeurCreeDto = mapper.map(editeurCreeDTO, EditeurCreeDto.class);
        editeurService.addEditeur(editeurCreeDto);
    }

    @PostMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void modificationEditeur (@PathVariable String id, @RequestBody EditeurModifieWebDto editeurModifieDTO) throws MailDoublonException {
        EditeurModifieDto editeurModifieDto = mapper.map(editeurModifieDTO, EditeurModifieDto.class);
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
        editeurService.deleteEditeur(id);
    }


}
