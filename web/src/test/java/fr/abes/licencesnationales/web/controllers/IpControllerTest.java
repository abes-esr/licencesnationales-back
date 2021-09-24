package fr.abes.licencesnationales.web.controllers;

import fr.abes.licencesnationales.LicencesNationalesAPIApplicationTests;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.web.security.services.FiltrerAccesServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class IpControllerTest extends LicencesNationalesAPIApplicationTests {
    @InjectMocks
    protected IpController controller;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private FiltrerAccesServices filtrerAccesServices;

    @Test
    @DisplayName("test récupération Ip d'un établissement")
    @WithMockUser
    void testGetIp() throws Exception {
        StatutIpEntity statutIp = new StatutIpEntity(1, "Nouvelle");
        ContactEntity contactEntity = new ContactEntity("nom1", "prenom1", "adresse1", "BP1", "00000", "ville1", "cedex1", "0000000000", "mail1@test.com", "mdp1");
        EtablissementEntity entity = new EtablissementEntity(1, "nomEtab1", "123456789", new TypeEtablissementEntity(2, "En validation"), "123456", contactEntity);
        IpEntity ipv4 = new IpV4(1, "1.1.1.1", "commentaireIP1");
        ipv4.setStatut(statutIp);
        entity.ajouterIp(ipv4);
        IpEntity ipv6 = new IpV6(2, "5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFD", "commentaireIP2");
        ipv6.setStatut(statutIp);
        entity.ajouterIp(ipv6);

        Mockito.doNothing().when(filtrerAccesServices).autoriserServicesParSiren("123456789");
        Mockito.when(etablissementService.getFirstBySiren("123456789")).thenReturn(entity);

        this.mockMvc.perform(get("/v1/ip/123456789")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].ip").value("1.1.1.1"))
                .andExpect(jsonPath("$.[0].typeAcces").value("ip"))
                .andExpect(jsonPath("$.[0].typeIp").value("IpV4"))
                .andExpect(jsonPath("$.[0].commentaires").value("commentaireIP1"))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].ip").value("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFD"))
                .andExpect(jsonPath("$.[1].typeAcces").value("range"))
                .andExpect(jsonPath("$.[1].typeIp").value("IpV6"))
                .andExpect(jsonPath("$.[1].commentaires").value("commentaireIP2"))

                ;
    }
}
