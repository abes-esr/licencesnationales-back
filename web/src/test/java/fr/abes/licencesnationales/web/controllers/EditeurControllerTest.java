/*
package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.services.EditeurService;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.dto.editeur.ContactCommercialEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.ContactTechniqueEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class EditeurControllerTest extends LicencesNationalesAPIApplicationTests {


    @MockBean
    private EditeurService editeurService;

    @MockBean
    private EtablissementService etablissementService;

    @Autowired
    private ObjectMapper mapper;




    @Test
    @DisplayName("test nouvel editeur sans être admin")
    @WithMockUser // on ne precise pas role admin
    public void testNouvelEditeurPasAdmin() throws Exception {
        EditeurCreeWebDto editeurCreeWebDto = new EditeurCreeWebDto();

        this.mockMvc.perform(put("/v1/ln/editeur/")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurCreeWebDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test nouvel editeur")
    @WithMockUser(authorities = {"admin"})
    public void testNouvelEditeur() throws Exception {
        EditeurCreeWebDto editeurCreeWebDto = new EditeurCreeWebDto();

        editeurCreeWebDto.setNomEditeur("NomEditeurTestD");
        editeurCreeWebDto.setIdentifiantEditeur("1238975");
        editeurCreeWebDto.setAdresseEditeur("adresse TestD");

        ArrayList groupesEtabRelies = new ArrayList();
        groupesEtabRelies.add("EPCI");
        groupesEtabRelies.add("Ecole de Management");

        editeurCreeWebDto.setGroupesEtabRelies(groupesEtabRelies);

        Set<ContactCommercialEditeurWebDto> cc = new HashSet<>();
        Set<ContactTechniqueEditeurWebDto> ct = new HashSet<>();

        ContactCommercialEditeurWebDto ccA  = new ContactCommercialEditeurWebDto();
        ccA.nomContactCommercial = "nomccATestD";
        ccA.prenomContactCommercial = "prenomccATestD";
        ccA.mailContactCommercial = "mailccATestD";

        ContactCommercialEditeurWebDto ccB  = new ContactCommercialEditeurWebDto();
        ccB.nomContactCommercial = "nomccBTestD";
        ccB.prenomContactCommercial = "prenomccBTestD";
        ccB.mailContactCommercial = "mailccBTestD";

        cc.add(ccA);
        cc.add(ccB);

        ContactTechniqueEditeurWebDto ctA  = new ContactTechniqueEditeurWebDto();
        ctA.nomContactTechnique = "nomctATestD";
        ctA.prenomContactTechnique = "prenomctATestD";
        ctA.mailContactTechnique = "mailctATestD";

        ContactTechniqueEditeurWebDto ctB  = new ContactTechniqueEditeurWebDto();
        ctB.nomContactTechnique = "nomctBTestD";
        ctB.prenomContactTechnique = "prenomctBTestD";
        ctB.mailContactTechnique = "mailctBTestD";

        ct.add(ctA);
        ct.add(ctB);

        editeurCreeWebDto.setListeContactCommercialEditeur(cc);
        editeurCreeWebDto.setListeContactTechniqueEditeur(ct);


        Mockito.when(etablissementService.checkDoublonMail(Mockito.any(),Mockito.any())).thenReturn(false);

        this.mockMvc.perform(put("/v1/ln/editeur/")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurCreeWebDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test nouvel editeur avec erreur sur mail doublon")
    @WithMockUser(authorities = {"admin"})
    public void testNouvelEditeurDoublonMail() throws Exception {
        EditeurCreeWebDto editeurCreeWebDto = new EditeurCreeWebDto();

        editeurCreeWebDto.setNomEditeur("NomEditeurTestD");
        editeurCreeWebDto.setIdentifiantEditeur("1238975");
        editeurCreeWebDto.setAdresseEditeur("adresse TestD");

        ArrayList groupesEtabRelies = new ArrayList();
        groupesEtabRelies.add("EPCI");
        groupesEtabRelies.add("Ecole de Management");

        editeurCreeWebDto.setGroupesEtabRelies(groupesEtabRelies);

        Set<ContactCommercialEditeurWebDto> cc = new HashSet<>();
        Set<ContactTechniqueEditeurWebDto> ct = new HashSet<>();

        ContactCommercialEditeurWebDto ccA  = new ContactCommercialEditeurWebDto();
        ccA.nomContactCommercial = "nomccATestD";
        ccA.prenomContactCommercial = "prenomccATestD";
        ccA.mailContactCommercial = "mailccATestD";

        ContactCommercialEditeurWebDto ccB  = new ContactCommercialEditeurWebDto();
        ccB.nomContactCommercial = "nomccBTestD";
        ccB.prenomContactCommercial = "prenomccBTestD";
        ccB.mailContactCommercial = "mailccBTestD";

        cc.add(ccA);
        cc.add(ccB);

        ContactTechniqueEditeurWebDto ctA  = new ContactTechniqueEditeurWebDto();
        ctA.nomContactTechnique = "nomctATestD";
        ctA.prenomContactTechnique = "prenomctATestD";
        ctA.mailContactTechnique = "mailctATestD";

        ContactTechniqueEditeurWebDto ctB  = new ContactTechniqueEditeurWebDto();
        ctB.nomContactTechnique = "nomctBTestD";
        ctB.prenomContactTechnique = "prenomctBTestD";
        ctB.mailContactTechnique = "mailctBTestD";

        ct.add(ctA);
        ct.add(ctB);

        editeurCreeWebDto.setListeContactCommercialEditeur(cc);
        editeurCreeWebDto.setListeContactTechniqueEditeur(ct);

        Mockito.doThrow(new MailDoublonException("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.")).when(editeurService).save(Mockito.any());

        this.mockMvc.perform(put("/v1/ln/editeur/")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(editeurCreeWebDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail."));

    }


    @Test
    @DisplayName("test liste editeur")
    @WithMockUser(authorities = {"admin"})
    public void testListEditeur() throws Exception {

        //Editeur A
        EditeurEntity editeurEntityA = new EditeurEntity();

        editeurEntityA.setNomEditeur("editeurEntityA");
        editeurEntityA.setIdentifiantEditeur("1238975");
        editeurEntityA.setAdresseEditeur("adresse editeurEntityA");
        editeurEntityA.setDateCreation(new Date());

        ArrayList groupesEtabReliesA = new ArrayList();
        groupesEtabReliesA.add("EPCI");
        groupesEtabReliesA.add("Ecole de Management");


        Set<ContactCommercialEditeurEntity> setccEA = new HashSet<>();
        Set<ContactTechniqueEditeurEntity> setctEA = new HashSet<>();

        ContactCommercialEditeurEntity accEA  = new ContactCommercialEditeurEntity();
        accEA.setNomContact("nomccEA");
        accEA.setPrenomContact("prenomccEA");
        accEA.setMailContact("mailccEA");

        ContactCommercialEditeurEntity bccEA  = new ContactCommercialEditeurEntity();
        bccEA.setNomContact("nomccB");
        bccEA.setPrenomContact("prenomccB");
        bccEA.setMailContact("mailccB");

        setccEA.add(accEA);
        setccEA.add(bccEA);

        ContactTechniqueEditeurEntity actEA  = new ContactTechniqueEditeurEntity();
        actEA.setNomContact("nomctA");
        actEA.setPrenomContact("prenomctA");
        actEA.setMailContact("mailctA");

        ContactTechniqueEditeurEntity bctEA  = new ContactTechniqueEditeurEntity();
        bctEA.setNomContact("nomctB");
        bctEA.setPrenomContact("prenomctB");
        bctEA.setMailContact("mailctB");

        setctEA.add(actEA);
        setctEA.add(bctEA);

        //editeurEntityA.setContactCommercialEditeurEntities(setccEA);
        //editeurEntityA.setContactTechniqueEditeurEntities(setctEA);

        //editeur B

        EditeurEntity editeurEntityB = new EditeurEntity();

        editeurEntityB.setNomEditeur("editeurEntityB");
        editeurEntityB.setIdentifiantEditeur("1238975646");
        editeurEntityB.setAdresseEditeur("adresse editeurEntityB");
        editeurEntityB.setDateCreation(new Date());

        ArrayList groupesEtabReliesB = new ArrayList();
        groupesEtabReliesB.add("EPCI");
        groupesEtabReliesB.add("Ecole de Management");


        Set<ContactCommercialEditeurEntity> setccEB = new HashSet<>();
        Set<ContactTechniqueEditeurEntity> setctEB = new HashSet<>();

        ContactCommercialEditeurEntity accEB  = new ContactCommercialEditeurEntity();
        accEB.setNomContact("nomccEA");
        accEB.setPrenomContact("prenomccEA");
        accEB.setMailContact("mailccEA");

        ContactCommercialEditeurEntity bccEB  = new ContactCommercialEditeurEntity();
        bccEB.setNomContact("nomccB");
        bccEB.setPrenomContact("prenomccB");
        bccEB.setMailContact("mailccB");

        setccEB.add(accEA);
        setccEB.add(bccEA);

        ContactTechniqueEditeurEntity actEB  = new ContactTechniqueEditeurEntity();
        actEB.setNomContact("nomctA");
        actEB.setPrenomContact("prenomctA");
        actEB.setMailContact("mailctA");

        ContactTechniqueEditeurEntity bctEB  = new ContactTechniqueEditeurEntity();
        bctEB.setNomContact("nomctB");
        bctEB.setPrenomContact("prenomctB");
        bctEB.setMailContact("mailctB");

        setctEB.add(actEB);
        setctEB.add(bctEB);

        editeurEntityB.setContactCommercialEditeurEntities(setccEB);
        editeurEntityB.setContactTechniqueEditeurEntities(setctEB);

        List<EditeurEntity> editeursList = new ArrayList<>();
        editeursList.add(editeurEntityA);
        editeursList.add(editeurEntityB);

        Mockito.when(editeurService.findAllEditeur()).thenReturn(editeursList);

        this.mockMvc.perform(get("/v1/ln/editeur/getListEditeurs")).andExpect(status().isOk());
    }

}
*/
