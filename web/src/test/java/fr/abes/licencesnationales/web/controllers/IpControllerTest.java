package fr.abes.licencesnationales.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.SirenIntrouvableException;
import fr.abes.licencesnationales.core.exception.UnknownIpException;
import fr.abes.licencesnationales.core.services.*;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import oracle.jdbc.driver.Const;
import org.hamcrest.core.AnyOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.oneOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class IpControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    protected IpController controller;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private FiltrerAccesServices filtrerAccesServices;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EventService eventService;

    @MockBean
    private IpService ipService;

    @MockBean
    private ReferenceService referenceService;


    @Test
    @DisplayName("test récupération Ip d'un établissement")
    @WithMockUser
    void testGetIp() throws Exception {
        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);
        IpEntity ipv4 = new IpV4(1, "1.1.1.1", "commentaireIP1", statutIp);
        ipv4.setStatut(statutIp);
        entity.ajouterIp(ipv4);
        IpEntity ipv6 = new IpV6(2, "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFD", "commentaireIP2", statutIp);
        ipv6.setStatut(statutIp);
        entity.ajouterIp(ipv6);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        this.mockMvc.perform(get("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", oneOf(1, 2)))
                .andExpect(jsonPath("$.[0].ip", oneOf("1.1.1.1", "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFD")))
                .andExpect(jsonPath("$.[0].commentaires", oneOf("commentaireIP1", "commentaireIP2")));
    }

    @Test
    @DisplayName("test Ajout IP")
    @WithMockUser
    void testAjoutIp() throws Exception {
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "[{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}]";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        json = "[" +
                "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "},\n" +
                "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"2.2.2.2\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}\n" +
                "]";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test Ajout IP avec doublon")
    @WithMockUser
    void testAjoutIpAvecDoublon() throws Exception {
        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);
        IpEntity ipEntity = new IpV4("1.1.1.1", "test", statutIp);
        entity.ajouterIp(ipEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.doNothing().when(emailService).constructAccesCreeEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);
        Mockito.when(ipService.isIpAlreadyExists(Mockito.any(IpV4.class))).thenReturn(true);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "[{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}]";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.debugMessage").value("[L'IP 1.1.1.1 est déjà utilisée]"));

        json = "[" +
                "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "},\n" +
                "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"2.2.2.2\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}\n" +
                "]";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.debugMessage").value("[L'IP 1.1.1.1 est déjà utilisée, L'IP 2.2.2.2 est déjà utilisée]"));
    }

    @Test
    @DisplayName("Test modification IP")
    @WithMockUser
    void testModificationIp() throws Exception {
        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);
        IpEntity ipEntity = new IpV4(1, "1.1.1.1", "test", statutIp);
        entity.ajouterIp(ipEntity);

        Mockito.when(ipService.getEtablissementByIp(1)).thenReturn(entity);
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.doNothing().when(emailService).constructAccesModifieEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(ipService.getFirstById(1)).thenReturn(ipEntity);

        StringBuilder json = new StringBuilder("{\n");
        json.append("\"typeIp\":\"IPV4\",\n");
        json.append("\"role\":\"etab\",\n");
        json.append("\"ip\":\"1.1.1.1\",\n");
        json.append("\"commentaires\":\"test\"\n");
        json.append("}");

        this.mockMvc.perform(post("/v1/ip/1")
                .contentType(MediaType.APPLICATION_JSON).content(json.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test modification IP admin")
    @WithMockUser(authorities = {"admin"})
    void testModificationIpAdmin() throws Exception {
        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);
        IpEntity ipEntity = new IpV6(1, "1111:1111:1111:1111:1111:1111:1111:1111", "test", statutIp);
        entity.ajouterIp(ipEntity);

        Mockito.when(ipService.getEtablissementByIp(1)).thenReturn(entity);
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.doNothing().when(emailService).constructAccesModifieEmail(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(ipService.getFirstById(1)).thenReturn(ipEntity);
        Mockito.when(referenceService.findStatutByLibelle(Mockito.anyString())).thenReturn(new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation"));

        StringBuilder json = new StringBuilder("{\n");
        json.append("\"typeIp\":\"IPV6\",\n");
        json.append("\"role\":\"etab\",\n");
        json.append("\"ip\":\"1111:1111:1111:1111:1111:1111:1111:1111\",\n");
        json.append("\"commentaires\":\"test\",\n");
        json.append("\"statut\":\"En validation\"\n");
        json.append("}");

        this.mockMvc.perform(post("/v1/ip/1")
                .contentType(MediaType.APPLICATION_JSON).content(json.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test validation IP")
    @WithMockUser(authorities = {"admin"})
    void testValiderIp() throws Exception {
        StatutIpEntity statutIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        List<Integer> listIps = Arrays.asList(1, 2, 3);
        IpEntity entity1 = new IpV4(1, "1.1.1.1", "test", statutIp);
        IpEntity entity2 = new IpV6(2, "1111:1111:1111:1111:1111:1111:1111:1111", "test2", statutIp);
        IpEntity entity3 = new IpV4(3, "1.1-10.2.3", "test3", statutIp);
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.when(ipService.getFirstById(1)).thenReturn(entity1);
        Mockito.when(ipService.getFirstById(2)).thenReturn(entity2);
        Mockito.when(ipService.getFirstById(3)).thenReturn(entity3);

        this.mockMvc.perform(post("/v1/ip/valider/")
        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(listIps)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test suppression IP")
    @WithMockUser(authorities = {"admin"})
    void testSupprimerIp() throws Exception {
        Mockito.doNothing().when(eventService).save(Mockito.any());
        this.mockMvc.perform(delete("/v1/ip/1"))
                .andExpect(status().isOk());
    }
}
