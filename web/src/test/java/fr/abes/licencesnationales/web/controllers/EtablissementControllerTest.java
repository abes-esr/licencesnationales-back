package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementCreeListener;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementModifieListener;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import fr.abes.licencesnationales.web.dto.etablissement.creation.ContactCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeSansCaptchaWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.fusion.EtablissementFusionneWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.ContactModifieWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieAdminWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieUserWebDto;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsImpl;
import fr.abes.licencesnationales.web.security.services.impl.UserDetailsServiceImpl;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import org.assertj.core.util.Lists;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    private EtablissementRepository dao;

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
    private ReferenceService referenceService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

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
        Mockito.when(etablissementService.existeSiren("123456789")).thenReturn(false);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(false);
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(listenerCreation).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailAdmin(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailUser(Mockito.any());

        this.mockMvc.perform(put("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_CREATIONETAB_OK))
                .andExpect(status().isOk());


    }

    @Test
    @DisplayName("test création de compte avec doublon Siren")
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
        Mockito.when(etablissementService.existeSiren("123456789")).thenReturn(true);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(false);
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(listenerCreation).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailAdmin(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailUser(Mockito.any());

        this.mockMvc.perform(put("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+Constant.SIREN_DOUBLON))
                .andExpect(jsonPath("$.debugMessage").value(Constant.SIREN_DOUBLON));
    }

    @Test
    @DisplayName("test création de compte avec doublon Mail")
    public void testCreationCompteDoublonMail() throws Exception {
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
        Mockito.when(etablissementService.existeSiren("123456789")).thenReturn(false);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(true);
        Mockito.when(reCaptchaService.verify(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(listenerCreation).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailAdmin(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(emailService).constructCreationCompteEmailUser(Mockito.any());

        this.mockMvc.perform(put("/v1/etablissements")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+Constant.ERROR_DOUBLON_MAIL))
                .andExpect(jsonPath("$.debugMessage").value(Constant.ERROR_DOUBLON_MAIL));
    }

    @Test
    @DisplayName("test modification établissement succès admin")
    @WithMockUser
    void testEditEtablissementAdmin() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");

        EtablissementModifieAdminWebDto etab = new EtablissementModifieAdminWebDto();
        etab.setNom("testNomEtab");
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
        etab.setContact(contact);

        ContactEntity contactInBdd = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity etabInBdd = new EtablissementEntity(1, "testNom", "123456789", type, "123456", contactInBdd);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etabInBdd);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(false);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        ContactEntity contactEntity = new ContactEntity("testNom", "testPrenom", "testAdresse", "testBP", "testCP", "testVille", "testCedex", "0000000000", "test@test.com", "12345*:KKk");
        EtablissementEntity entity = new EtablissementEntity(1, "testNomEtab", "123456789", type, "12345", contactEntity);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isOk());

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("etab");
        this.mockMvc.perform(post("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(Constant.ERROR_CREDENTIALS + "L'opération ne peut être effectuée que par un administrateur"))
                .andExpect(jsonPath("$.debugMessage").value(Constant.OPERATION_QUE_PAR_ADMIN));

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
        etab.setContact(contact);

        ContactEntity contactInBdd = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity etabInBdd = new EtablissementEntity(1, "testNom", "123456789", type, "123456", contactInBdd);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etabInBdd);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(false);

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(emailService).constructModificationMailAdmin(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_MODIFETAB_OK))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("test modification établissement user avec Accès interdit")
    void testEditEtablissementUserWithAccesInterdit() throws Exception {
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
        etab.setContact(contact);

        ContactEntity contactInBdd = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity etabInBdd = new EtablissementEntity(1, "testNom", "123456789", type, "123456", contactInBdd);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etabInBdd);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(false);

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());

        Mockito.doNothing().when(eventService).save(Mockito.any());

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenThrow(new AccesInterditException(Constant.ACCES_INTERDIT));
        this.mockMvc.perform(post("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("test modification établissement user avec mauvais siren")
    void testEditEtablissementUserWithWrongSiren() throws Exception {
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
        etab.setContact(contact);

        ContactEntity contactInBdd = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity etabInBdd = new EtablissementEntity(1, "testNom", "123456789", type, "123456", contactInBdd);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etabInBdd);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(false);

        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());

        Mockito.doNothing().when(eventService).save(Mockito.any());

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("987654321");
        this.mockMvc.perform(post("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("test modification établissement user avec doublon email")
    @WithMockUser(authorities = {"etab"})
    void testEditEtablissementUserWithDoublonMail() throws Exception {
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
        etab.setContact(contact);

        ContactEntity contactInBdd = new ContactEntity(1, "nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity etabInBdd = new EtablissementEntity(1, "testNom", "123456789", type, "123456", contactInBdd);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etabInBdd);
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(true);

        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());

        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(etab)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE + Constant.ERROR_DOUBLON_MAIL))
                .andExpect(jsonPath("$.debugMessage").value(Constant.ERROR_DOUBLON_MAIL));

    }

    @Test
    @DisplayName("test fusion établissement")
    @WithMockUser(authorities = {"admin"})
    void testFusionEtab() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        EtablissementFusionneWebDto dto = new EtablissementFusionneWebDto();
        dto.setSirenFusionnes(Lists.newArrayList("123456789", "987654321"));
        EtablissementCreeSansCaptchaWebDto dtoNouvelEtab = new EtablissementCreeSansCaptchaWebDto();
        dtoNouvelEtab.setNom("nomEtab");
        dtoNouvelEtab.setTypeEtablissement("Nouveau");
        dtoNouvelEtab.setSiren("654987321");
        ContactCreeWebDto dtoContact = new ContactCreeWebDto();
        dtoContact.setNom("nomContact");
        dtoContact.setPrenom("prenomContact");
        dtoContact.setMail("test@test.com");
        dtoContact.setMotDePasse("motDePasseContact");
        dtoContact.setAdresse("adresseContact");
        dtoContact.setCodePostal("00000");
        dtoContact.setVille("VilleContact");
        dtoContact.setBoitePostale("BPContact");
        dtoContact.setTelephone("0000000000");
        dtoContact.setCedex("cedexContact");
        dtoNouvelEtab.setContact(dtoContact);
        dto.setNouveauEtab(dtoNouvelEtab);

        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity1 = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity1 = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity1);
        entity1.ajouterIp(new IpV4(1, "1.1.1.1", "commentaireIP1", statutIp));
        entity1.ajouterIp(new IpV6(2, "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFD", "commentaireIP2", statutIp));

        ContactEntity contactEntity2 = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity entity2 = new EtablissementEntity(1, "nomEtab2", "987654321", new TypeEtablissementEntity(3, "Validé"), "654321", contactEntity2);
        entity2.ajouterIp(new IpV4(3, "2.2.2.2", "commentaireIP3", statutIp));
        entity2.ajouterIp(new IpV6(4, "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF", "commentaireIP4", statutIp));

        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity1);
        Mockito.when(etablissementService.getFirstBySiren("987654321")).thenReturn(entity2);
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(etablissementService).deleteBySiren("123456789");
        Mockito.doNothing().when(etablissementService).deleteBySiren("987654321");
        Mockito.doNothing().when(etablissementService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/fusion")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_FUSIONETAB_OK))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("test fusion établissement siren doublon")
    @WithMockUser(authorities = {"admin"})
    void testFusionEtabDoublon() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        EtablissementFusionneWebDto dto = new EtablissementFusionneWebDto();
        dto.setSirenFusionnes(Lists.newArrayList("123456789", "987654321"));
        EtablissementCreeSansCaptchaWebDto dtoNouvelEtab = new EtablissementCreeSansCaptchaWebDto();
        dtoNouvelEtab.setNom("nomEtab");
        dtoNouvelEtab.setTypeEtablissement("Nouveau");
        dtoNouvelEtab.setSiren("654987321");
        ContactCreeWebDto dtoContact = new ContactCreeWebDto();
        dtoContact.setNom("nomContact");
        dtoContact.setPrenom("prenomContact");
        dtoContact.setMail("test@test.com");
        dtoContact.setMotDePasse("motDePasseContact");
        dtoContact.setAdresse("adresseContact");
        dtoContact.setCodePostal("00000");
        dtoContact.setVille("VilleContact");
        dtoContact.setBoitePostale("BPContact");
        dtoContact.setTelephone("0000000000");
        dtoContact.setCedex("cedexContact");
        dtoNouvelEtab.setContact(dtoContact);
        dto.setNouveauEtab(dtoNouvelEtab);

        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity1 = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity1 = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity1);

        ContactEntity contactEntity2 = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity entity2 = new EtablissementEntity(1, "nomEtab2", "987654321", new TypeEtablissementEntity(3, "Validé"), "654321", contactEntity2);

        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(etablissementService.existeSiren("654987321")).thenReturn(true);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity1);
        Mockito.when(etablissementService.getFirstBySiren("987654321")).thenReturn(entity2);
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(etablissementService).deleteBySiren("123456789");
        Mockito.doNothing().when(etablissementService).deleteBySiren("987654321");
        Mockito.doNothing().when(etablissementService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/fusion")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE + String.format(Constant.ERROR_ETAB_DOUBLON,"654987321")))
                .andExpect(jsonPath("$.debugMessage").exists());

    }

    @Test
    @DisplayName("test fusion établissement mail doublon")
    @WithMockUser(authorities = {"admin"})
    void testFusionEtabMailDoublon() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        EtablissementFusionneWebDto dto = new EtablissementFusionneWebDto();
        dto.setSirenFusionnes(Lists.newArrayList("123456789", "987654321"));
        EtablissementCreeSansCaptchaWebDto dtoNouvelEtab = new EtablissementCreeSansCaptchaWebDto();
        dtoNouvelEtab.setNom("nomEtab");
        dtoNouvelEtab.setTypeEtablissement("Nouveau");
        dtoNouvelEtab.setSiren("654987321");
        ContactCreeWebDto dtoContact = new ContactCreeWebDto();
        dtoContact.setNom("nomContact");
        dtoContact.setPrenom("prenomContact");
        dtoContact.setMail("test@test.com");
        dtoContact.setMotDePasse("motDePasseContact");
        dtoContact.setAdresse("adresseContact");
        dtoContact.setCodePostal("00000");
        dtoContact.setVille("VilleContact");
        dtoContact.setBoitePostale("BPContact");
        dtoContact.setTelephone("0000000000");
        dtoContact.setCedex("cedexContact");
        dtoNouvelEtab.setContact(dtoContact);
        dto.setNouveauEtab(dtoNouvelEtab);

        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity1 = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity1 = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity1);

        ContactEntity contactEntity2 = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity entity2 = new EtablissementEntity(1, "nomEtab2", "987654321", new TypeEtablissementEntity(3, "Validé"), "654321", contactEntity2);

        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(etablissementService.existeMail("test@test.com")).thenReturn(true);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity1);
        Mockito.when(etablissementService.getFirstBySiren("987654321")).thenReturn(entity2);
        Mockito.doNothing().when(listenerModification).onApplicationEvent(Mockito.any());
        Mockito.doNothing().when(etablissementService).deleteBySiren("123456789");
        Mockito.doNothing().when(etablissementService).deleteBySiren("987654321");
        Mockito.doNothing().when(etablissementService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/fusion")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE + String.format(Constant.ERROR_MAIL_DOUBLON,"test@test.com")))
                .andExpect(jsonPath("$.debugMessage").exists());

    }


    @Test
    @DisplayName("test suppression Etablissement")
    @WithMockUser(authorities = {"admin"})
    void testSuppressionEtablissement() throws Exception {
        String siren = "123456789";
        String nomEtab = "nomEtab";
        String mail = "mail2@test.com";
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", mail, "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, nomEtab, "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etab);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(etablissementService).deleteBySiren(siren);
        Mockito.when(userDetailsService.loadUser(etab)).thenReturn(new UserDetailsImpl(etab));
        Mockito.doNothing().when(emailService).constructSuppressionCompteMailUserEtAdmin(Mockito.any(), Mockito.anyString(), Mockito.anyString());

        this.mockMvc.perform(delete("/v1/etablissements/123456789")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_SUPPETAB_OK))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test validation établissement")
    @WithMockUser(authorities = {"admin"})
    void testValidationEtablissement() throws Exception {
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);

        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etab);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(userDetailsService.loadUser(etab)).thenReturn(new UserDetailsImpl(etab));
        Mockito.doNothing().when(emailService).constructValidationCompteMailUser(etab.getNom(), etab.getContact().getMail());
        Mockito.doNothing().when(emailService).constructValidationCompteMailAdmin(etab.getNom(), etab.getContact().getMail());

        this.mockMvc.perform(post("/v1/etablissements/validation/123456789"))
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_VALIDATIONETAB_OK))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test validation établissement avec mauvais statut")
    @WithMockUser(authorities = {"admin"})
    void testValidationEtablissementWithWrongStatut() throws Exception {
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);
        etab.setValide(true);
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etab);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/etablissements/validation/123456789"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(Constant.ERROR_STATUT_IP +Constant.DEJA_VALIDE))
                .andExpect(jsonPath("$.debugMessage").value(Constant.DEJA_VALIDE));
    }

    @Test
    @DisplayName("test récupération info établissement User")
    @WithMockUser(authorities = {"etab"})
    void testGetEtab() throws Exception {
        ContactEntity contact = new ContactEntity(1, "nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail@mail.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);

        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etab);
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");

        mockMvc.perform(get("/v1/etablissements/123456789")).andExpect(status().isOk())
                .andExpect(jsonPath("$.contact.nom").value("nom2"))
                .andExpect(jsonPath("$.contact.prenom").value("prenom2"))
                .andExpect(jsonPath("$.contact.mail").value("mail@mail.com"))
                .andExpect(jsonPath("$.contact.telephone").value("1111111111"))
                .andExpect(jsonPath("$.contact.adresse").value("adresse2"))
                .andExpect(jsonPath("$.contact.boitePostale").value("BP2"))
                .andExpect(jsonPath("$.contact.codePostal").value("11111"))
                .andExpect(jsonPath("$.contact.cedex").value("cedex2"))
                .andExpect(jsonPath("$.contact.ville").value("ville2"))
                .andExpect(jsonPath("$.contact.role").value("etab"));
    }

    @Test
    @DisplayName("test récupération info établissement Admin")
    @WithMockUser(authorities = {"admin"})
    void testGetEtabAdmin() throws Exception {
        Calendar dateJour = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        ContactEntity contact = new ContactEntity(1, "nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail@mail.com", "mdp2");
        contact.setRole("admin");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "testType"), "123456", contact);
        etab.setDateCreation(dateJour.getTime());
        StatutIpEntity statutIpEntity = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation à envoyer");
        IpEntity ip1 = new IpV4("1.1.1.1", "test", statutIpEntity);
        ip1.setDateModification(new GregorianCalendar(2020, 1, 1).getTime());
        IpEntity ip2 = new IpV4("2.2.2.2", "test", statutIpEntity);
        ip2.setDateModification(new GregorianCalendar(2021, 2, 2).getTime());
        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);

        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etab);
        Mockito.when(userDetailsService.loadUser(etab)).thenReturn(new UserDetailsImpl(etab));
        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");

        mockMvc.perform(get("/v1/etablissements/123456789")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("nomEtab"))
                .andExpect(jsonPath("$.siren").value("123456789"))
                .andExpect(jsonPath("$.idAbes").value("123456"))
                .andExpect(jsonPath("$.typeEtablissement").value("testType"))
                .andExpect(jsonPath("$.dateCreation").value(format.format(dateJour.getTime())))
                .andExpect(jsonPath("$.statut").value("Nouveau"))
                .andExpect(jsonPath("$.statutIps").value(Constant.STATUT_ETAB_ATTENTEATTESTATION))
                .andExpect(jsonPath("$.dateModificationDerniereIp").value("02-03-2021"))
                .andExpect(jsonPath("$.contact.nom").value("nom2"))
                .andExpect(jsonPath("$.contact.prenom").value("prenom2"))
                .andExpect(jsonPath("$.contact.mail").value("mail@mail.com"))
                .andExpect(jsonPath("$.contact.telephone").value("1111111111"))
                .andExpect(jsonPath("$.contact.adresse").value("adresse2"))
                .andExpect(jsonPath("$.contact.boitePostale").value("BP2"))
                .andExpect(jsonPath("$.contact.codePostal").value("11111"))
                .andExpect(jsonPath("$.contact.cedex").value("cedex2"))
                .andExpect(jsonPath("$.contact.ville").value("ville2"))
                .andExpect(jsonPath("$.contact.role").value("etab"));
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

        this.mockMvc.perform(get("/v1/etablissements")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("test export etablissement User")
    @WithMockUser(authorities = {"etab"})
    void testExportEtablissementUser() throws Exception {
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "11111", "ville", "cedex", "1111111111", "mail2@mail.com", "mdp");
        contact.setRole("admin");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456789", contact);

        List listeEtabOut = new ArrayList();
        listeEtabOut.add(etab);
        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("etab");
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(dao.findAllBySirenIn(Mockito.any())).thenReturn(listeEtabOut);

        String fileContent = "ID Abes;Siren;Nom de l'établissement;Type de l'établissement;Adresse de l'établissement;Téléphone contact;Nom et prénom contact;Adresse mail contact;IP\r\n";
        fileContent += "123456789;123456789;nomEtab;validé;adresse 11111 ville BP cedex;1111111111;nom prenom;mail2@mail.com\r\n";
        String json = "[]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/etablissements/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }

    @Test
    @DisplayName("test export etablissement User avec IPs")
    @WithMockUser(authorities = {"etab"})
    void testExportEtablissementUserWithIp() throws Exception {
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "11111", "ville", "cedex", "1111111111", "mail2@mail.com", "mdp");
        contact.setRole("admin");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456789", contact);
        etab.ajouterIp(new IpV4("192.162.0.1","commentaire",new StatutIpEntity(1,"test")));
        etab.ajouterIp(new IpV4("192.162.0.2","commentaire",new StatutIpEntity(1,"test")));


        List listeEtabOut = new ArrayList();
        listeEtabOut.add(etab);
        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("etab");
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(dao.findAllBySirenIn(Mockito.any())).thenReturn(listeEtabOut);

        String fileContent = "ID Abes;Siren;Nom de l'établissement;Type de l'établissement;Adresse de l'établissement;Téléphone contact;Nom et prénom contact;Adresse mail contact;IP\r\n";
        fileContent += "123456789;123456789;nomEtab;validé;adresse 11111 ville BP cedex;1111111111;nom prenom;mail2@mail.com;192.162.0.1\r\n";
        fileContent += "123456789;123456789;nomEtab;validé;adresse 11111 ville BP cedex;1111111111;nom prenom;mail2@mail.com;192.162.0.2\r\n";
        String json = "[]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/etablissements/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }

    @Test
    @DisplayName("test export etablissement Admin")
    @WithMockUser(authorities = {"admin"})
    void testExportEtablissementAdmin() throws Exception {
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "11111", "ville", "cedex", "1111111111", "mail2@mail.com", "mdp");
        contact.setRole("admin");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456789", contact);

        ContactEntity contact2 = new ContactEntity(2, "nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail@mail.com", "mdp2");
        contact2.setRole("etab");
        EtablissementEntity etab2 = new EtablissementEntity(2, "nomEtab2", "111111111", new TypeEtablissementEntity(3, "validé"), "123456", contact2);

        List listeEtabOut = new ArrayList();
        listeEtabOut.add(etab);
        listeEtabOut.add(etab2);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.when(dao.findAllBySirenIn(Mockito.any())).thenReturn(listeEtabOut);

        String fileContent = "ID Abes;Siren;Nom de l'établissement;Type de l'établissement;Adresse de l'établissement;Ville;Téléphone contact;Nom et prénom contact;Adresse mail contact;IP\r\n";
        fileContent += "123456789;123456789;nomEtab;validé;adresse 11111 BP cedex;ville;1111111111;nom prenom;mail2@mail.com\r\n";
        fileContent += "123456;111111111;nomEtab2;validé;adresse2 11111 BP2 cedex2;ville2;1111111111;nom2 prenom2;mail@mail.com\r\n";
        String json = "[\"123456789\",\"111111111\"]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/etablissements/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }

    @Test
    @DisplayName("test export etablissement Admin avec IP")
    @WithMockUser(authorities = {"admin"})
    void testExportEtablissementAdminWithIp() throws Exception {
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "11111", "ville", "cedex", "1111111111", "mail2@mail.com", "mdp");
        contact.setRole("admin");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456789", contact);
        etab.ajouterIp(new IpV4("192.162.0.1","commentaire",new StatutIpEntity(1,"test")));
        etab.ajouterIp(new IpV4("192.162.0.2","commentaire",new StatutIpEntity(1,"test")));

        ContactEntity contact2 = new ContactEntity(2, "nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail@mail.com", "mdp2");
        contact2.setRole("etab");
        EtablissementEntity etab2 = new EtablissementEntity(2, "nomEtab2", "111111111", new TypeEtablissementEntity(3, "validé"), "123456", contact2);

        List listeEtabOut = new ArrayList();
        listeEtabOut.add(etab);
        listeEtabOut.add(etab2);

        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("admin");
        Mockito.when(dao.findAllBySirenIn(Mockito.any())).thenReturn(listeEtabOut);

        String fileContent = "ID Abes;Siren;Nom de l'établissement;Type de l'établissement;Adresse de l'établissement;Ville;Téléphone contact;Nom et prénom contact;Adresse mail contact;IP\r\n";
        fileContent += "123456789;123456789;nomEtab;validé;adresse 11111 BP cedex;ville;1111111111;nom prenom;mail2@mail.com;192.162.0.1;192.162.0.2\r\n";
        fileContent += "123456;111111111;nomEtab2;validé;adresse2 11111 BP2 cedex2;ville2;1111111111;nom2 prenom2;mail@mail.com\r\n";
        String json = "[\"123456789\",\"111111111\"]";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/etablissements/export").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }
}
