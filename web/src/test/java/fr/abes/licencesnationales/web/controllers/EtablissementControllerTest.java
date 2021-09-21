package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementCreeListener;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementModifieListener;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private FiltrerAccesServices filtrerAccesServices;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private EventService eventService;

    @MockBean
    private EtablissementCreeListener listenerCreation;

    @MockBean
    private EtablissementModifieListener listenerModification;

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
        Mockito.doNothing().when(listenerCreation).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailAdmin(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailUser(Mockito.anyString());

        this.mockMvc.perform(put("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("test modification établissement succès admin")
    @WithMockUser
    void testEditEtablissementAdmin() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");

        EtablissementModifieAdminWebDto etab = new EtablissementModifieAdminWebDto();
        etab.setName("testNomEtab");
        etab.setTypeEtablissement("Nouveau");
        etab.setSiren("123456789");
        ContactModifieWebDto contact = new ContactModifieWebDto();
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
        etab.setContact(contact);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        ContactEntity contactEntity = new ContactEntity("testNom", "testPrenom", "testAdresse", "testBP", "testCP", "testVille", "testCedex", "0000000000", "test@test.com", "12345*:KKk");
        EtablissementEntity entity = new EtablissementEntity(1, "testNomEtab", "123456789", type, "12345", contactEntity);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        Mockito.when(typeEtablissementRepository.findFirstByLibelle(Mockito.anyString())).thenReturn(Optional.of(type));
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isOk());

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("etab");
        this.mockMvc.perform(post("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Credentials not valid"))
                .andExpect(jsonPath("$.debugMessage").value("L'opération ne peut être effectuée que par un administrateur"));

    }

    @Test
    @DisplayName("test modification établissement user")
    @WithMockUser(authorities = {"etab"})
    void testEditEtablissementUser() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");

        EtablissementModifieUserWebDto etab = new EtablissementModifieUserWebDto();
        etab.setSiren("123456789");
        ContactModifieWebDto contact = new ContactModifieWebDto();
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
        etab.setContact(contact);

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(typeEtablissementRepository.findFirstByLibelle(Mockito.anyString())).thenReturn(Optional.of(type));
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        ContactEntity contactEntity = new ContactEntity("testNom", "testPrenom", "testAdresse", "testBP", "testCP", "testVille", "testCedex", "0000000000", "test@test.com", "12345*:KKk");
        EtablissementEntity entity = new EtablissementEntity(1, "testNomEtab", "123456789", type, "12345", contactEntity);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);


        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isOk());

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenThrow(new AccesInterditException("Acces interdit"));
        this.mockMvc.perform(post("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Credentials not valid"))
                .andExpect(jsonPath("$.debugMessage").value("Acces interdit"));
    }

    @Test
    @DisplayName("test liste établissements")
    @WithMockUser(authorities = {"admin"})
    void testListEtab() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity();
        type.setId(1);
        type.setLibelle("typeEtab");
        List<EtablissementEntity> etabList = new ArrayList<>();

        ContactEntity contact = new ContactEntity("testNom", "testPrenom", "testAdresse", "testBP", "testCedex", "testCP", "testVille", "0000000000", "test@test.com", "12345*:KKk");

        etabList.add(new EtablissementEntity(1, "testNom", "123456789", type, "1", contact));
        etabList.add(new EtablissementEntity(2, "testNom", "123456789", type, "1", contact));
        etabList.add(new EtablissementEntity(3, "testNom", "123456789", type, "1", contact));
        Mockito.when(etablissementService.findAll()).thenReturn(etabList);

        this.mockMvc.perform(get("/v1/ln/etablissement/getListEtab")).andExpect(status().isOk());
        this.mockMvc.perform(post("/v1/ln/etablissement/getListEtab")).andExpect(status().isMethodNotAllowed());
    }

}
