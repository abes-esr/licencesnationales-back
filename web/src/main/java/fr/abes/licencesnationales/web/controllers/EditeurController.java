package fr.abes.licencesnationales.web.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurSupprimeEventEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.services.*;
import fr.abes.licencesnationales.core.services.export.ExportEditeur;
import fr.abes.licencesnationales.core.services.export.editeur.*;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurWebDto;
import fr.abes.licencesnationales.web.exception.SendMailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/v1/editeurs")
public class EditeurController extends AbstractController {

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
    private EmailService emailService;

    @Autowired
    private ExportEditeur exportEditeur;

    @Autowired
    private ExportEditeurDeletedAccess exportEditeurDeletedAccess;

    @Autowired
    private ExportEditeurDeletedInstitutions exportEditeurDeletedInstitutions;

    @Autowired
    private ExportEditeurListAll exportEditeurListAll;

    @Autowired
    private ExportEditeurMergedInstitutions exportEditeurMergedInstitutions;

    @Autowired
    private ExportEditeurModifiedInstitutions exportEditeurModifiedInstitutions;

    @Autowired
    private ExportEditeurNewAccess exportEditeurNewAccess;

    @Autowired
    private ExportEditeurNewInstitutions exportEditeurNewInstitutions;

    @Autowired
    private ExportEditeurSplitInstitutions exportEditeurSplitInstitutions;

    @Autowired
    private DateEnvoiEditeurRepository dateEnvoiEditeurRepository;

    @Value("${ln.dest.notif.admin}")
    private String mailAdmin;

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
    public void edit(@PathVariable Integer id, @Valid @RequestBody EditeurModifieWebDto editeurModifieWebDto) throws UnknownTypeEtablissementException, JsonProcessingException {
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

    @PostMapping(value = "/export")
    @PreAuthorize("hasAuthority('admin')")
    public void exportEditeur(@RequestBody List<Integer> ids, HttpServletResponse response) throws IOException{
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=\"export.csv\"");
        InputStream stream = exportEditeur.generateCsv(ids);
        IOUtils.copy(stream , response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping(value = "/exportMensuelEditeur")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> envoiMensuel() throws SendMailException {
        List<String> erreurMails = new ArrayList<>();
        editeurService.findAllEditeur().stream().forEach(editeurEntity -> {
            Map<String, ByteArrayInputStream> mapFichiers = new HashMap<>();
            List<Integer> listeTypesEditeurs = editeurEntity.getTypeEtablissements().stream().map(t -> t.getId()).collect(Collectors.toList());
            mapFichiers.put("deletedAccess.csv", exportEditeurDeletedAccess.generateCsv(listeTypesEditeurs));
            mapFichiers.put("deletedInstitutions.csv", exportEditeurDeletedInstitutions.generateCsv(listeTypesEditeurs));
            mapFichiers.put("listAll.csv", exportEditeurListAll.generateCsv(listeTypesEditeurs));
            mapFichiers.put("mergedInstitutions.csv", exportEditeurMergedInstitutions.generateCsv(listeTypesEditeurs));
            mapFichiers.put("modifiedInstitutions.csv", exportEditeurModifiedInstitutions.generateCsv(listeTypesEditeurs));
            mapFichiers.put("newAccess.csv", exportEditeurNewAccess.generateCsv(listeTypesEditeurs));
            mapFichiers.put("newInstitutions.csv", exportEditeurNewInstitutions.generateCsv(listeTypesEditeurs));
            mapFichiers.put("splitInstitutions.csv", exportEditeurSplitInstitutions.generateCsv(listeTypesEditeurs));
            try {
                //on envoie le mail aux contacts techniques (concaténation des adresse mails avec un ;) avec copie à l'admin LN
                emailService.constructEnvoiFichierEditeurs(editeurEntity.getContactsTechniques().stream().map(c -> c.getMail()).collect(Collectors.joining(";")), mailAdmin, mapFichiers);
            } catch (IOException e) {
                erreurMails.add(editeurEntity.getNom());
            }
        });
        if (erreurMails.size() != 0) {
            throw new SendMailException("aux éditeurs : " + String.join(",", erreurMails));
        }
        //sauvegarde de la date d'envoi des fichiers aux éditeurs pour envois ultérieurs
        Date dateEnvoi = Calendar.getInstance().getTime();
        dateEnvoiEditeurRepository.save(new DateEnvoiEditeurEntity(dateEnvoi));
        emailService.constructEnvoiFichierEditeursConfirmationAdmin(mailAdmin, dateEnvoi);
        return buildResponseEntity(Constant.MESSAGE_ENVOI_OK);
    }
}
