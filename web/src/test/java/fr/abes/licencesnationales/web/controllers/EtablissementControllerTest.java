package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementCreeListener;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.web.dto.etablissement.ContactCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class EtablissementControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    protected EtablissementController controller;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private ReCaptchaService reCaptchaService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private EventService eventService;

    @MockBean
    private EtablissementCreeListener listener;

    @MockBean
    private TypeEtablissementRepository typeEtablissementRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("test création de compte")
    public void testCreationCompte() throws Exception {
        EtablissementCreeWebDto dto = new EtablissementCreeWebDto();
        dto.setRecaptcha("ksdjfklsklfjhskjdfhklf");
        dto.setName("Etab de test 32");
        dto.setSiren("123456789");
        dto.setTypeEtablissement("EPIC/EPST");
        ContactCreeWebDto contact = new ContactCreeWebDto();
        contact.setNom("testNom");
        contact.setPrenom("testPrenom");
        contact.setAdresse("testAdresse");
        contact.setBoitePostale("testBP");
        contact.setCedex("testCedex");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        contact.setMotDePasse("12345*:KKk");
        dto.setContact(contact);

        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction("creationCompte");
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(listener).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailAdmin(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailUser(Mockito.any(), Mockito.anyString());

        this.mockMvc.perform(put("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("test création de compte avec erreur sur mail doublon")
    public void testCreationCompteDoublonMail() throws Exception {
        EtablissementCreeWebDto dto = new EtablissementCreeWebDto();
        dto.setRecaptcha("ksdjfklsklfjhskjdfhklf");
        dto.setName("Etab de test 32");
        dto.setSiren("123456789");
        dto.setTypeEtablissement("EPIC/EPST");
        ContactCreeWebDto contact = new ContactCreeWebDto();
        contact.setNom("testNom");
        contact.setPrenom("testPrenom");
        contact.setMotDePasse("12345*:KKk");
        contact.setAdresse("testAdresse");
        contact.setBoitePostale("testBP");
        contact.setCedex("testCedex");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        dto.setContact(contact);

        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction("creationCompte");
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.when(etablissementService.existeSiren(Mockito.anyString())).thenReturn(false);
        Mockito.when(etablissementService.existeMail(Mockito.anyString())).thenReturn(true);

        this.mockMvc.perform(post("/v1/ln/etablissement/creationCompte")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail."));

    }

    @Test
    @DisplayName("test création de compte avec erreur sur siren doublon")
    public void testCreationCompteDoublonSiren() throws Exception {
        EtablissementCreeWebDto dto = new EtablissementCreeWebDto();
        dto.setRecaptcha("ksdjfklsklfjhskjdfhklf");
        dto.setName("Etab de test 32");
        dto.setSiren("123456789");
        dto.setTypeEtablissement("EPIC/EPST");
        ContactCreeWebDto contact = new ContactCreeWebDto();
        contact.setNom("testNom");
        contact.setPrenom("testPrenom");
        contact.setAdresse("testAdresse");
        contact.setBoitePostale("testBP");
        contact.setCedex("testCedex");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        contact.setMotDePasse("12345*:KKk");
        dto.setContact(contact);

        ReCaptchaResponse response = new ReCaptchaResponse();
        response.setSuccess(true);
        response.setAction("creationCompte");
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.when(etablissementService.existeSiren(Mockito.anyString())).thenReturn(true);
        Mockito.when(etablissementService.existeMail(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(post("/v1/ln/etablissement/creationCompte")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Cet établissement existe déjà."));

    }

    @Test
    @DisplayName("test liste établissements")
    @WithMockUser(authorities = {"admin"})
    public void testListEtab() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity();
        type.setId(1);
        type.setLibelle("typeEtab");
        List<EtablissementEntity> etabList = new ArrayList<>();

        ContactEntity contact = new ContactEntity("testNom","testPrenom","testAdresse","testBP","testCedex","testCP","testVille","0000000000","test@test.com","12345*:KKk");

        etabList.add(new EtablissementEntity(1, "testNom", "123456789", type, "1", contact));
        etabList.add(new EtablissementEntity(2, "testNom", "123456789", type, "1", contact));
        etabList.add(new EtablissementEntity(3, "testNom", "123456789", type, "1", contact));
        Mockito.when(etablissementService.findAll()).thenReturn(etabList);

        this.mockMvc.perform(get("/v1/ln/etablissement/getListEtab")).andExpect(status().isOk());
        this.mockMvc.perform(post("/v1/ln/etablissement/getListEtab")).andExpect(status().isMethodNotAllowed());
    }

}
