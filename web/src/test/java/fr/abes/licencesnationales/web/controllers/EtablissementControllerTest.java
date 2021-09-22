package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.exception.AccesInterditException;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementCreeListener;
import fr.abes.licencesnationales.core.listener.etablissement.EtablissementModifieListener;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import fr.abes.licencesnationales.web.service.ReCaptchaService;
import org.assertj.core.util.Lists;
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

import java.util.ArrayList;
import java.util.Date;
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
    private ReferenceService referenceService;

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

        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
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
        Mockito.when(referenceService.findTypeEtabByLibelle(Mockito.anyString())).thenReturn(type);
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

        ContactEntity contactEntity1 = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity1 = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity1);
        entity1.ajouterIp(new IpV4(1, "1.1.1.1", "commentaireIP1"));
        entity1.ajouterIp(new IpV6(2, "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFD", "commentaireIP2"));

        ContactEntity contactEntity2 = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity entity2 = new EtablissementEntity(1, "nomEtab2", "987654321", new TypeEtablissementEntity(3, "Validé"), "654321", contactEntity2);
        entity2.ajouterIp(new IpV4(3, "2.2.2.2", "commentaireIP3"));
        entity2.ajouterIp(new IpV6(4, "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF", "commentaireIP4"));

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
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test suppression Etablissement")
    @WithMockUser(authorities = {"admin"})
    void testSuppressionEtablissement() {
        MotifSuppressionWebDto motif = new MotifSuppressionWebDto();
        motif.setMotif("Motif suppression");

        String siren = "123456789";
        String nomEtab = "nomEtab";
        String mail = "mail2@test.com";
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", mail, "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, nomEtab, "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(Mockito.any());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(etablissementService).deleteBySiren(siren);
        Mockito.doNothing().when(emailService).constructSuppressionMail(motif.getMotif(), etab.getName(), etab.getContact().getMail());
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

}
