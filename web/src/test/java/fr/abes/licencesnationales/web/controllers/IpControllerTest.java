package fr.abes.licencesnationales.web.controllers;

import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.UnknownIpException;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.*;
import fr.abes.licencesnationales.web.dto.ip.gestion.ActionIp;
import fr.abes.licencesnationales.web.dto.ip.gestion.IpGereeWebDto;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.List;

import static org.hamcrest.Matchers.oneOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class IpControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    protected IpController controller;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private IpRepository dao;

    @MockBean
    private FiltrerAccesServices filtrerAccesServices;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EventService eventService;

    @MockBean
    private IpEventRepository ipEventRepository;

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

        IpCreeEventEntity event = new IpCreeEventEntity(this, "1.1.1.1", "test");
        event.setIpId(2);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);
        Mockito.when(ipEventRepository.save(Mockito.any())).thenReturn(event);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_AJOUTIP_OK));
    }

    @Test
    @DisplayName("test Ajout IP sans IP")
    @WithMockUser
    void testAjoutIpSansIp() throws Exception {
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+Constant.ERROR_IP_IP_OBLIGATOIRE))
                .andExpect(jsonPath("$.debugMessage").exists());
    }
    @Test
    @DisplayName("test Ajout IP Nulle")
    @WithMockUser
    void testAjoutIpNulle() throws Exception {
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+Constant.IP_NOTNULL))
                .andExpect(jsonPath("$.debugMessage").exists());
    }


    @Test
    @DisplayName("test Ajout IP Reservee")
    @WithMockUser
    void testAjoutIpReservee() throws Exception {
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"0.0.0.0\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+String.format(Constant.ERROR_IP_RESERVEES,"0.0.0.0/32","0.0.0.0/32")))
                .andExpect(jsonPath("$.debugMessage").exists());
    }

    @Test
    @DisplayName("test Ajout IP Mauvais format")
    @WithMockUser
    void testAjoutIpMauvaisFormat() throws Exception {
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+Constant.ERROR_IPV4_INVALIDE+"1.1.1"))
                .andExpect(jsonPath("$.debugMessage").exists());
    }

    @Test
    @DisplayName("test Ajout Range IP Mauvais format")
    @WithMockUser
    void testAjoutRangeIpMauvaisFormat() throws Exception {
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1-1.1-0\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+Constant.ERROR_IPV4_INVALIDE+"1.1.1-1.1-0"))
                .andExpect(jsonPath("$.debugMessage").exists());
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
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);
        Mockito.when(ipService.isIpAlreadyExists(Mockito.any(IpV4.class))).thenReturn(true);

        //obligé de créer un JSON manuellement car l'instanciation d'un IpAjouteeWebDto ne permet pas de récupérer le type
        String json = "{\n" +
                "\"typeIp\":\"IPV4\",\n" +
                "\"ip\":\"1.1.1.1\",\n" +
                "\"commentaires\":\"test\"\n" +
                "}";

        this.mockMvc.perform(put("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_SAISIE+String.format(Constant.ERROR_IP_DOUBLON,"1.1.1.1")))
                .andExpect(jsonPath("$.debugMessage").exists());
    }

    @Test
    @DisplayName("test suppression IP")
    @WithMockUser(authorities = {"etab"})
    void testSupprimerIp() throws Exception {
        StatutIpEntity statut = new StatutIpEntity(1, "Nouvelle IP");
        IpEntity ip = new IpV4(1, "1.1.1.1", "test", statut);
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);
        ip.setEtablissement(etab);
        Mockito.when(ipService.getFirstById(1)).thenReturn(ip);
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.doNothing().when(eventService).save(Mockito.any());
        this.mockMvc.perform(delete("/v1/ip/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(Constant.MESSAGE_SUPPIP_OK));
    }

    @Test
    @DisplayName("test suppression IP Inconnue")
    @WithMockUser(authorities = {"admin"})
    void testSupprimerIpInconnue() throws Exception {
        Mockito.when(ipService.getFirstById(1)).thenThrow(new UnknownIpException(String.format(Constant.ERROR_IP_EXISTE_PAS,1)));
        Mockito.doNothing().when(eventService).save(Mockito.any());
        this.mockMvc.perform(delete("/v1/ip/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Constant.ERROR_IP_INCONNUE+String.format(Constant.ERROR_IP_EXISTE_PAS,1)))
                .andExpect(jsonPath("$.debugMessage").exists());
    }

    @Test
    @DisplayName("test suppression IP sans auth")
    void testSupprimerIpNoUser() throws Exception {
        this.mockMvc.perform(delete("/v1/ip/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("test gestion Ip")
    @WithMockUser(authorities = {"admin"})
    void testGestionIp() throws Exception {
        IpGereeWebDto dto1 = new IpGereeWebDto();
        dto1.setAction(ActionIp.REJETER);
        dto1.setIdIp(1);

        IpGereeWebDto dto2 = new IpGereeWebDto();
        dto2.setAction(ActionIp.VALIDER);
        dto2.setIdIp(2);

        IpGereeWebDto dto3 = new IpGereeWebDto();
        dto3.setAction(ActionIp.SUPPRIMER);
        dto3.setIdIp(3);
        dto3.setCommentaire("test");

        List<IpGereeWebDto> listIps = new ArrayList<>();
        listIps.add(dto1);
        listIps.add(dto2);
        listIps.add(dto3);

        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "11111", "ville", "cedex", "1111111111", "mail2@mail.com", "mdp");
        contact.setRole("etab");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456789", contact);

        StatutIpEntity statut = new StatutIpEntity(1, "Nouveau");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(etab);
        Mockito.doNothing().when(eventService).save(Mockito.any());
        Mockito.doNothing().when(emailService).constructBilanRecapActionIpUser(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        IpEntity ipv41 = new IpV4(1, "1.1.1.1", "test", statut);
        ipv41.setEtablissement(etab);
        IpEntity ipv42 = new IpV4(1, "2.2.2.2", "test", statut);
        ipv42.setEtablissement(etab);
        IpEntity ipv43 = new IpV4(1, "3.3.3.3", "test", statut);
        ipv43.setEtablissement(etab);
        Mockito.when(ipService.getFirstById(1)).thenReturn(ipv41);
        Mockito.when(ipService.getFirstById(2)).thenReturn(ipv42);
        Mockito.when(ipService.getFirstById(3)).thenReturn(ipv43);

        Mockito.doNothing().when(ipService).save(Mockito.any());

        this.mockMvc.perform(post("/v1/ip/gerer/123456789")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(listIps)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].ip", oneOf("1.1.1.1", "2.2.2.2 | test", "3.3.3.3")))
                .andExpect(jsonPath("$.[0].action", oneOf("validation", "suppression", "rejet")));
    }

    @Test
    @DisplayName("test gestion IP Utilisateur Etab")
    @WithMockUser(authorities = {"etab"})
    void testGestionIpWrongUser() throws Exception {
        IpGereeWebDto dto1 = new IpGereeWebDto();
        dto1.setAction(ActionIp.REJETER);
        dto1.setIdIp(1);

        IpGereeWebDto dto2 = new IpGereeWebDto();
        dto1.setAction(ActionIp.VALIDER);
        dto1.setIdIp(2);

        IpGereeWebDto dto3 = new IpGereeWebDto();
        dto1.setAction(ActionIp.SUPPRIMER);
        dto1.setIdIp(3);

        List<IpGereeWebDto> listIps = new ArrayList<>();
        listIps.add(dto1);
        listIps.add(dto2);
        listIps.add(dto3);

        this.mockMvc.perform(post("/v1/ip/gerer/111111111")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(listIps)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test gestion IP sans auth")
    void testGestionIpNoUser() throws Exception {
        IpGereeWebDto dto1 = new IpGereeWebDto();
        dto1.setAction(ActionIp.REJETER);
        dto1.setIdIp(1);

        IpGereeWebDto dto2 = new IpGereeWebDto();
        dto1.setAction(ActionIp.VALIDER);
        dto1.setIdIp(2);

        IpGereeWebDto dto3 = new IpGereeWebDto();
        dto1.setAction(ActionIp.SUPPRIMER);
        dto1.setIdIp(3);

        List<IpGereeWebDto> listIps = new ArrayList<>();
        listIps.add(dto1);
        listIps.add(dto2);
        listIps.add(dto3);

        this.mockMvc.perform(post("/v1/ip/gerer/111111111").content(mapper.writeValueAsString(listIps)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("test export IP")
    @WithMockUser(authorities = {"etab"})
    void testExportIp() throws Exception {
        Calendar dateDuJour = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "11111", "ville", "cedex", "1111111111", "mail2@mail.com", "mdp");
        contact.setRole("etab");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456789", contact);
        StatutIpEntity statutIp = new StatutIpEntity(1, "validé");
        IpV4 ip = new IpV4(1, "192.128.0.1", "test", statutIp);
        etab.ajouterIp(ip);
        List list = new ArrayList();
        list.add(ip);
        Mockito.when(filtrerAccesServices.getRoleFromSecurityContextUser()).thenReturn("etab");
        Mockito.when(filtrerAccesServices.getSirenFromSecurityContextUser()).thenReturn("123456789");
        Mockito.when(dao.findAllBySiren(Mockito.any())).thenReturn(list);

        String fileContent = "Date de saisie;Type d'IP;Valeur;Date de modification du statut;Statut;Commentaires\r\n";
        fileContent += format.format(dateDuJour.getTime())+";IP V4;192.128.0.1;;validé;test\r\n";
        String json = "[]";


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/ip/export/123456789").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();

        Assertions.assertEquals("text/csv;charset=UTF-8", result.getResponse().getContentType());
        Assertions.assertEquals(fileContent, result.getResponse().getContentAsString());

    }
}
