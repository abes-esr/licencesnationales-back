package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurSupprimeEventEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.services.EditeurService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import fr.abes.licencesnationales.core.services.export.ExportEditeur;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurWebDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/v1/editeurs")
public class EditeurController {

    @Autowired
    private UtilsMapper mapper;

    @Autowired
    private EditeurService editeurService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private EventService eventService;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private ExportEditeur exportEditeur;

    @PutMapping("/")
    @PreAuthorize("hasAuthority('admin')")
    public void creationEditeur(@Valid @RequestBody EditeurCreeWebDto editeurCreeWebDto) throws IOException, UnknownTypeEtablissementException {
        // On convertit la DTO web (Json) en objet métier d'événement de création d'éditeur
        EditeurCreeEventEntity event = mapper.map(editeurCreeWebDto, EditeurCreeEventEntity.class);
        event.setSource(this);
        //initialisation de la liste des types d'établissements par récupération dans la BDD
        for (String t : editeurCreeWebDto.getTypesEtablissements()) {
                event.addTypeEtab(referenceService.findTypeEtabByLibelle(t));
        }

        // On publie l'événement et on le sauvegarde
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
    }

    @PostMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void edit (@PathVariable Integer id, @Valid @RequestBody EditeurModifieWebDto editeurModifieWebDto) throws UnknownTypeEtablissementException, JsonProcessingException {
        editeurModifieWebDto.setId(id);
        EditeurModifieEventEntity event = mapper.map(editeurModifieWebDto, EditeurModifieEventEntity.class);
        event.setSource(this);
        //initialisation de la liste des types d'établissements par récupération dans la BDD
        for (String t : editeurModifieWebDto.getTypesEtablissements()) {
            event.addTypeEtab(referenceService.findTypeEtabByLibelle(t));
        }
        // On publie l'événement et on le sauvegarde
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void suppression(@PathVariable Integer id) throws JsonProcessingException {
        //on cherche l'editeur uniquement pour gérer le cas où il n'existe pas
        editeurService.getFirstEditeurById(id);

        EditeurSupprimeEventEntity event = new EditeurSupprimeEventEntity(this, id);
        applicationEventPublisher.publishEvent(event);
        eventService.save(event);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public EditeurWebDto get(@PathVariable Integer id) {
        EditeurEntity editeurEntity = editeurService.getFirstEditeurById(id);
        return mapper.map(editeurEntity, EditeurWebDto.class);
    }


    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('admin')")
    public List<EditeurWebDto> getListEditeurs() {
        return mapper.mapList(editeurService.findAllEditeur(), EditeurWebDto.class);
    }

    @GetMapping(value = "/export")
    @PreAuthorize("hasAuthority('admin')")
    public void exportEditeur(@RequestBody List<Integer> ids, HttpServletResponse response) throws IOException{
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=\"export.csv\"");
        InputStream stream = exportEditeur.generateCsv(ids);
        IOUtils.copy(stream , response.getOutputStream());
        response.flushBuffer();
    }
}
