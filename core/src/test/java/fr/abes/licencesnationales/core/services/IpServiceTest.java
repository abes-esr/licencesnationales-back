package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.exception.UnknownIpException;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.repository.ip.IpV4Repository;
import fr.abes.licencesnationales.core.repository.ip.IpV6Repository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IpService.class})
public class IpServiceTest {
    @Autowired
    private IpService ipService;

    @MockBean
    private IpRepository ipRepository;

    @MockBean
    private IpV4Repository ipV4Repository;

    @MockBean
    private IpV6Repository ipV6Repository;

    @Test
    @DisplayName("test IP V4 existante")
    void isIpV4AlreadyExists() throws IpException {
        StatutIpEntity statut = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        List<IpV4> ipV4List = new ArrayList<>();
        ipV4List.add(new IpV4("1-10.1.1.1", "test", statut));
        ipV4List.add(new IpV4("2.1-253.1.1", "test2", statut));

        Mockito.when(ipV4Repository.findAll()).thenReturn(ipV4List);

        IpV4 candidate = new IpV4("253.1.1.1", "test", statut);
        Assertions.assertFalse(ipService.isIpAlreadyExists(candidate));

        candidate = new IpV4("2.3.1.1", "test", statut);
        Assertions.assertTrue(ipService.isIpAlreadyExists(candidate));

        candidate = new IpV4("3-250.1.1.1", "test", statut);
        Assertions.assertTrue(ipService.isIpAlreadyExists(candidate));
    }

    @Test
    @DisplayName("test IP V6 existante")
    void isIpV6AlreadyExists() throws IpException {
        StatutIpEntity statut = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        List<IpV6> ipV6List = new ArrayList<>();
        ipV6List.add(new IpV6("1111:1111:1111:1111:1111:1111:1111-2222:1111-AAAA", "test", statut));
        ipV6List.add(new IpV6("2222:1111-AAAA:1111:1111:1111:1111:1111:1111", "test2", statut));

        Mockito.when(ipV6Repository.findAll()).thenReturn(ipV6List);

        IpV6 candidate = new IpV6("3333:1111:1111:1111:1111:1111:1111:1111", "test", statut);
        Assertions.assertFalse(ipService.isIpAlreadyExists(candidate));

        candidate = new IpV6("2222:2222:1111:1111:1111:1111:1111:1111", "test", statut);
        Assertions.assertTrue(ipService.isIpAlreadyExists(candidate));

        candidate = new IpV6("2222:9999-CCCC:1111:1111:1111:1111:1111:1111", "test", statut);
        Assertions.assertTrue(ipService.isIpAlreadyExists(candidate));
    }

    @Test
    @DisplayName("test getEtablissementByIp")
    void testGetEtablissementByIp() throws UnknownIpException, IpException {
        StatutIpEntity statut = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        IpEntity ip = new IpV4(1, "1.1.1.1", "test", statut);
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);
        ip.setEtablissement(etabIn);
        Mockito.when(ipRepository.getFirstById(1)).thenReturn(Optional.of(ip));

        Assertions.assertEquals(etabIn.getSiren(), ipService.getEtablissementByIp(1).getSiren());
        Assertions.assertEquals(etabIn.getTypeEtablissement().getLibelle(), ipService.getEtablissementByIp(1).getTypeEtablissement().getLibelle());
        Assertions.assertEquals(etabIn.getNom(), ipService.getEtablissementByIp(1).getNom());

        ip.setEtablissement(null);
        Assertions.assertThrows(UnknownEtablissementException.class, () -> ipService.getEtablissementByIp(1));

        Mockito.when(ipRepository.getFirstById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(UnknownIpException.class, () -> ipService.getEtablissementByIp(1));
    }

    @Test
    @DisplayName("test getFirstById")
    void testGetFirstById() throws IpException, UnknownIpException {
        StatutIpEntity statut = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        IpEntity ip = new IpV4(1, "1.1.1.1", "test", statut);
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);
        ip.setEtablissement(etabIn);

        Mockito.when(ipRepository.getFirstById(1)).thenReturn(Optional.of(ip));
        Assertions.assertEquals(ip.getIp(), ipService.getFirstById(1).getIp());
        Assertions.assertEquals(ip.getCommentaires(), ipService.getFirstById(1).getCommentaires());

        Mockito.when(ipRepository.getFirstById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(UnknownIpException.class, () -> ipService.getFirstById(1));
    }
}
