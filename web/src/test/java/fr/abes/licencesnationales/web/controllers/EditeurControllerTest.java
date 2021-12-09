package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.UnknownEditeurException;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import fr.abes.licencesnationales.core.services.EditeurService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import fr.abes.licencesnationales.web.dto.editeur.ContactEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class EditeurControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    private EditeurController controller;

    @MockBean
    private EditeurService editeurService;

    @MockBean
    private ReferenceService referenceService;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private FiltrerAccesServices filtrerAccesServices;

    @MockBean
    private EditeurRepository dao;

    @Test
    @DisplayName("test nouvel editeur sans être admin")
    @WithMockUser // on ne precise pas role admin
    public void testNouvelEditeurPasAdmin() throws Exception {
        EditeurCreeWebDto editeurCreeWebDto = new EditeurCreeWebDto();

        this.mockMvc.perform(put("/v1/editeurs/")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurCreeWebDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test nouvel editeur")
    @WithMockUser(authorities = {"admin"})
    public void testNouvelEditeur() throws Exception {
        EditeurCreeWebDto editeurCreeWebDto = new EditeurCreeWebDto();

        editeurCreeWebDto.setNom("NomEditeurTestD");
        editeurCreeWebDto.setIdentifiantBis("1238975");
        editeurCreeWebDto.setAdresse("adresse TestD");

        ArrayList typesEtablissements = new ArrayList();
        typesEtablissements.add("EPCI");
        typesEtablissements.add("Ecole de Management");

        editeurCreeWebDto.setTypesEtablissements(typesEtablissements);

        Set<ContactEditeurWebDto> cc = new HashSet<>();
        Set<ContactEditeurWebDto> ct = new HashSet<>();

        ContactEditeurWebDto ccA  = new ContactEditeurWebDto();
        ccA.nomContact = "nomccATestD";
        ccA.prenomContact = "prenomccATestD";
        ccA.mailContact = "mail@mail.com";

        ContactEditeurWebDto ccB  = new ContactEditeurWebDto();
        ccB.nomContact = "nomccBTestD";
        ccB.prenomContact = "prenomccBTestD";
        ccB.mailContact = "mail@mail.com";

        cc.add(ccA);
        cc.add(ccB);

        editeurCreeWebDto.setContactsCommerciaux(cc);
        editeurCreeWebDto.setContactsTechniques(ct);

        Mockito.when(referenceService.findTypeEtabByLibelle("EPCI")).thenReturn(new TypeEtablissementEntity(1, "EPCI"));
        Mockito.when(referenceService.findTypeEtabByLibelle("Ecole de management")).thenReturn(new TypeEtablissementEntity(2, "Ecole de management"));
        Mockito.doNothing().when(editeurService).checkDoublonMail(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(put("/v1/editeurs/")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurCreeWebDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test nouvel editeur avec erreur sur mail doublon")
    @WithMockUser(authorities = {"admin"})
    public void testNouvelEditeurDoublonMail() throws Exception {
        EditeurCreeWebDto editeurCreeWebDto = new EditeurCreeWebDto();

        editeurCreeWebDto.setNom("NomEditeurTestD");
        editeurCreeWebDto.setIdentifiantBis("1238975");
        editeurCreeWebDto.setAdresse("adresse TestD");

        ArrayList typesEtablissements = new ArrayList();
        typesEtablissements.add("EPCI");
        typesEtablissements.add("Ecole de Management");

        editeurCreeWebDto.setTypesEtablissements(typesEtablissements);

        Set<ContactEditeurWebDto> cc = new HashSet<>();
        Set<ContactEditeurWebDto> ct = new HashSet<>();

        ContactEditeurWebDto ccA  = new ContactEditeurWebDto();
        ccA.nomContact = "nomccATestD";
        ccA.prenomContact = "prenomccATestD";
        ccA.mailContact = "mail@mail.com";

        ContactEditeurWebDto ccB  = new ContactEditeurWebDto();
        ccB.nomContact = "nomccBTestD";
        ccB.prenomContact = "prenomccBTestD";
        ccB.mailContact = "mail@mail.com";

        cc.add(ccA);
        cc.add(ccB);

        editeurCreeWebDto.setContactsCommerciaux(cc);
        editeurCreeWebDto.setContactsTechniques(ct);

        Mockito.doThrow(new MailDoublonException(Constant.ERROR_DOUBLON_MAIL)).when(editeurService).save(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(put("/v1/editeurs/")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurCreeWebDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Erreur dans la saisie : " + Constant.ERROR_DOUBLON_MAIL));
    }

    @Test
    @DisplayName("test modification éditeur admin")
    @WithMockUser(authorities = {"admin"})
    void testModificationEditeur() throws Exception {
        EditeurModifieWebDto editeurModifieWebDto = new EditeurModifieWebDto();

        editeurModifieWebDto.setId(1);
        editeurModifieWebDto.setNom("NomEditeurTestD");
        editeurModifieWebDto.setIdentifiantBis("1238975");
        editeurModifieWebDto.setAdresse("adresse TestD");

        ArrayList typeEtablissements = new ArrayList();
        typeEtablissements.add("EPCI");
        typeEtablissements.add("Ecole de Management");

        editeurModifieWebDto.setTypesEtablissements(typeEtablissements);

        Set<ContactEditeurWebDto> cc = new HashSet<>();
        Set<ContactEditeurWebDto> ct = new HashSet<>();

        ContactEditeurWebDto ccA  = new ContactEditeurWebDto();
        ccA.nomContact = "nomccATestD";
        ccA.prenomContact = "prenomccATestD";
        ccA.mailContact = "mail@mail.com";

        ContactEditeurWebDto ccB  = new ContactEditeurWebDto();
        ccB.nomContact = "nomccBTestD";
        ccB.prenomContact = "prenomccBTestD";
        ccB.mailContact = "mail@mail.com";

        cc.add(ccA);
        cc.add(ccB);

        editeurModifieWebDto.setContactsCommerciaux(cc);
        editeurModifieWebDto.setContactsTechniques(ct);

        Mockito.when(referenceService.findTypeEtabByLibelle("EPCI")).thenReturn(new TypeEtablissementEntity(1, "EPCI"));
        Mockito.when(referenceService.findTypeEtabByLibelle("Ecole de management")).thenReturn(new TypeEtablissementEntity(2, "Ecole de management"));
        Mockito.doNothing().when(editeurService).checkDoublonMail(Mockito.any());
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/editeurs/1")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurModifieWebDto)))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("test liste editeur")
    @WithMockUser(authorities = {"admin"})
    public void testListEditeur() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> typeSet = new HashSet<>();
        typeSet.add(type);

        EditeurEntity editeurEntityA = new EditeurEntity(1, "editeurEntityA", "1238975", "adresse editeurEntityA", typeSet);

        EditeurEntity editeurEntityB = new EditeurEntity(2, "editeurEntityB", "1238975646", "adresse editeurEntityB", typeSet);

        List<EditeurEntity> editeursList = new ArrayList<>();
        editeursList.add(editeurEntityA);
        editeursList.add(editeurEntityB);

        Mockito.when(editeurService.findAllEditeur()).thenReturn(editeursList);

        this.mockMvc.perform(get("/v1/editeurs/")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("test get éditeur admin")
    @WithMockUser(authorities = {"admin"})
    void testGetEditeur() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);
        EditeurEntity entity = new EditeurEntity(1, "nom", "1238975", "adresse", setType);

        ContactEditeurEntity contactCommercial = new ContactCommercialEditeurEntity(1, "nomComm", "prenomComm", "mail@mail.com");
        ContactEditeurEntity contactTechnique = new ContactTechniqueEditeurEntity(2, "nomTech", "prenomTech", "mail@mail.tech");

        entity.ajouterContact(contactCommercial);
        entity.ajouterContact(contactTechnique);

        Mockito.when(editeurService.getFirstEditeurById(1)).thenReturn(entity);

        this.mockMvc.perform(get("/v1/editeurs/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("nom"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identifiantBis").value("1238975"))
                .andExpect(jsonPath("$.typesEtablissements.[0]").value("Nouveau"))
                .andExpect(jsonPath("$.adresse").value("adresse"))
                .andExpect(jsonPath("$.contactsCommerciaux.[0].id").value(1))
                .andExpect(jsonPath("$.contactsCommerciaux.[0].nom").value("nomComm"))
                .andExpect(jsonPath("$.contactsCommerciaux.[0].prenom").value("prenomComm"))
                .andExpect(jsonPath("$.contactsCommerciaux.[0].mail").value("mail@mail.com"))
                .andExpect(jsonPath("$.contactsTechniques.[0].id").value(2))
                .andExpect(jsonPath("$.contactsTechniques.[0].nom").value("nomTech"))
                .andExpect(jsonPath("$.contactsTechniques.[0].prenom").value("prenomTech"))
                .andExpect(jsonPath("$.contactsTechniques.[0].mail").value("mail@mail.tech"));
    }

    @Test
    @DisplayName("test get editeur utilisateur etab")
    @WithMockUser(authorities = {"etab"})
    void testGetEditeurWrongUser() throws Exception {
        this.mockMvc.perform(get("/v1/editeurs/1")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test get editeur sans user")
    void testGetEditeurNoUser() throws Exception {
        this.mockMvc.perform(get("/v1/editeurs/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("test suppression editeur admin avec erreur")
    @WithMockUser(authorities = {"admin"})
    void testSuppressionEditeurErreur() throws Exception {
        //cas éditeur inconnu
        Mockito.when(editeurService.getFirstEditeurById(1)).thenThrow(new UnknownEditeurException("id : 1"));
        this.mockMvc.perform(delete("/v1/editeurs/1")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Editeur inconnu : id : 1"))
                .andExpect(jsonPath("$.debugMessage").value("id : 1"));
    }

    @Test
    @DisplayName("test suppression editeur admin")
    @WithMockUser(authorities = {"admin"})
    void testSuppressionEditeur() throws Exception {
        Mockito.when(editeurService.getFirstEditeurById(1)).thenReturn(new EditeurEntity(1, "nom", "1238975", "adresse", null));
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(editeurService).deleteById(1);
        this.mockMvc.perform(delete("/v1/editeurs/1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("test suppression editeur etab")
    @WithMockUser(authorities = {"etab"})
    void testSuppressionEditeurWrongUser() throws Exception {
        this.mockMvc.perform(delete("/v1/editeurs/1")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test suppression editeur sans user")
    void testSuppressionEditeurNoUser() throws Exception {
        this.mockMvc.perform(delete("/v1/editeurs/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("test export editeur Admin 2 editeurs avec 1 seul contact")
    @WithMockUser(authorities = {"admin"})
    void testExportEditeurAdminDeuxEditeursUnSeulContact() throws Exception {
        ContactEditeurEntity contact = new ContactTechniqueEditeurEntity(1,"nomTech", "prenomTech", "mailTech@mail.com");
        TypeEtablissementEntity type = new TypeEtablissementEntity(4,"test");
        Set types = new HashSet();
        types.add(type);
        EditeurEntity editeur = new EditeurEntity(1,"nomEditeur","identifiant","adresse",types);
        editeur.ajouterContact(contact);
        ContactEditeurEntity contact2 = new ContactCommercialEditeurEntity(2,"nomCom", "prenomCom", "mailCom@mail.com");
        EditeurEntity editeur2 = new EditeurEntity(2,"nomEditeur2","identifiant2","adresse2",types);
        editeur2.ajouterContact(contact2);

        List listEditeur = new ArrayList();
        listEditeur.add(editeur);
        listEditeur.add(editeur2);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.when(dao.getAllByIdIn(Mockito.any())).thenReturn(listEditeur);

        String fileContent = "ID éditeur;Nom de l'éditeur;Adresse de l'éditeur;Nom(s) et Prenom(s) des contacts;Adresse(s) mail(s) des contacts;Type de contact\r\n";
        fileContent += "identifiant;nomEditeur;adresse;nomTech prenomTech;mailTech@mail.com;Technique\r\n";
        fileContent += "identifiant2;nomEditeur2;adresse2;nomCom prenomCom;mailCom@mail.com;Commercial\r\n";
        String json = "[1,2]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/editeurs/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }


    @Test
    @DisplayName("test export editeur Admin 1 seul editeurs avec 1 seul contact")
    @WithMockUser(authorities = {"admin"})
    void testExportEditeurAdminUnSeulEditeurUnSeulContact() throws Exception {
        ContactEditeurEntity contact = new ContactTechniqueEditeurEntity(1,"nomTech", "prenomTech", "mailTech@mail.com");
        TypeEtablissementEntity type = new TypeEtablissementEntity(1,"test");
        Set types = new HashSet();
        types.add(type);
        EditeurEntity editeur = new EditeurEntity(1,"nomEditeur","identifiant","adresse",types);
        editeur.ajouterContact(contact);

        List listEditeur = new ArrayList();
        listEditeur.add(editeur);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.when(dao.getAllByIdIn(Mockito.any())).thenReturn(listEditeur);

        String fileContent = "ID éditeur;Nom de l'éditeur;Adresse de l'éditeur;Nom(s) et Prenom(s) des contacts;Adresse(s) mail(s) des contacts;Type de contact\r\n";
        fileContent += "identifiant;nomEditeur;adresse;nomTech prenomTech;mailTech@mail.com;Technique\r\n";
        String json = "[1]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/editeurs/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }


    @Test
    @DisplayName("test export editeur Admin 1 editeurs avec 2 contact")
    @WithMockUser(authorities = {"admin"})
    void testExportEditeurAdminUnSeulEditeurDeuxContact() throws Exception {
        ContactEditeurEntity contact = new ContactTechniqueEditeurEntity(1,"nomTech", "prenomTech", "mailTech@mail.com");
        TypeEtablissementEntity type = new TypeEtablissementEntity(4,"test");
        Set types = new HashSet();
        types.add(type);
        EditeurEntity editeur = new EditeurEntity(1,"nomEditeur","identifiant","adresse",types);
        ContactEditeurEntity contact2 = new ContactCommercialEditeurEntity(2,"nomCom", "prenomCom", "mailCom@mail.com");
        editeur.ajouterContact(contact);
        editeur.ajouterContact(contact2);

        List listEditeur = new ArrayList();
        listEditeur.add(editeur);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.when(dao.getAllByIdIn(Mockito.any())).thenReturn(listEditeur);

        String fileContent = "ID éditeur;Nom de l'éditeur;Adresse de l'éditeur;Nom(s) et Prenom(s) des contacts;Adresse(s) mail(s) des contacts;Type de contact\r\n";
        fileContent += "identifiant;nomEditeur;adresse;nomCom prenomCom;mailCom@mail.com;Commercial\r\n";
        fileContent += "identifiant;nomEditeur;adresse;nomTech prenomTech;mailTech@mail.com;Technique\r\n";
        String json = "[1]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/editeurs/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }
}

