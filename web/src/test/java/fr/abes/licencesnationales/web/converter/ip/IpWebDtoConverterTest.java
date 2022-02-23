package fr.abes.licencesnationales.web.converter.ip;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpRejeteeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpValideeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import fr.abes.licencesnationales.web.dto.ip.creation.Ipv4AjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.creation.Ipv6AjouteeWebDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, IpWebDtoConverter.class, ObjectMapper.class})
class IpWebDtoConverterTest {
    @Autowired
    private UtilsMapper mapper;

    @Test
    @DisplayName("Test convertisseur IpV4 / IpWebDto")
    void testEntityIpV4WebDto() throws IpException {
        IpWebDto dto;
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV4(1, "1.1.1.1", "test", statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1.1.1.1", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IPV4", dto.getTypeIp());
        Assertions.assertEquals("ip", dto.getTypeAcces());
    }

    @Test
    @DisplayName("test convertisseur range IpV4 / IpWebDto")
    void testEntityIpRangeV4WebDto() throws IpException {
        IpWebDto dto;
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV4(1, "1.1-10.1.1", "test", statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1.1-10.1.1", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IPV4", dto.getTypeIp());
        Assertions.assertEquals("range", dto.getTypeAcces());

    }

    @Test
    @DisplayName("Test convertisseur IpV6 / IpWebDto")
    void testEntityIpV6WebDto() throws IpException {
        IpWebDto dto;
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV6(1, "1111:1111:1111:1111:1111:1111:1111:1111", "test", statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IPV6", dto.getTypeIp());
        Assertions.assertEquals("ip", dto.getTypeAcces());
    }

    @Test
    @DisplayName("Test convertisseur range IpV6 / IpWebDto")
    void testEntityIpRangeV6WebDto() throws IpException {
        IpWebDto dto;
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV6(1, "1111:1111-2222:1111:1111:1111-2222:1111:1111:1111", "test", statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1111:1111-2222:1111:1111:1111-2222:1111:1111:1111", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IPV6", dto.getTypeIp());
        Assertions.assertEquals("range", dto.getTypeAcces());
    }

    @Test
    @DisplayName("test convertisseur IpAjoutee / IpCreeEvent")
    void testIpAjoutee() {
        Ipv4AjouteeWebDto ipv4 = new Ipv4AjouteeWebDto();
        ipv4.setIp("1.1.1.1");
        ipv4.setCommentaires("test");

        IpCreeEventEntity ipCreeEventEntity = mapper.map(ipv4, IpCreeEventEntity.class);

        Assertions.assertEquals("test", ipCreeEventEntity.getCommentaires());
        Assertions.assertEquals("1.1.1.1", ipCreeEventEntity.getIp());
        Assertions.assertEquals(IpType.IPV4, ipCreeEventEntity.getTypeIp());
        Assertions.assertEquals("ip", ipCreeEventEntity.getTypeAcces());

        ipv4 = new Ipv4AjouteeWebDto();
        ipv4.setIp("1.1-5.1.1");
        ipv4.setCommentaires("test");

        ipCreeEventEntity = mapper.map(ipv4, IpCreeEventEntity.class);

        Assertions.assertEquals("test", ipCreeEventEntity.getCommentaires());
        Assertions.assertEquals("1.1-5.1.1", ipCreeEventEntity.getIp());
        Assertions.assertEquals(IpType.IPV4, ipCreeEventEntity.getTypeIp());
        Assertions.assertEquals("range", ipCreeEventEntity.getTypeAcces());

        Ipv6AjouteeWebDto ipv6 = new Ipv6AjouteeWebDto();
        ipv6.setIp("1111:1111:1111:1111:1111:1111:1111:1111");
        ipv6.setCommentaires("test");

        ipCreeEventEntity = mapper.map(ipv6, IpCreeEventEntity.class);

        Assertions.assertEquals("test", ipCreeEventEntity.getCommentaires());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", ipCreeEventEntity.getIp());
        Assertions.assertEquals(IpType.IPV6, ipCreeEventEntity.getTypeIp());
        Assertions.assertEquals("ip", ipCreeEventEntity.getTypeAcces());

        ipv6 = new Ipv6AjouteeWebDto();
        ipv6.setIp("1111:1111-2222:1111:1111:1111:1111:1111:1111");
        ipv6.setCommentaires("test");

        ipCreeEventEntity = mapper.map(ipv6, IpCreeEventEntity.class);

        Assertions.assertEquals("test", ipCreeEventEntity.getCommentaires());
        Assertions.assertEquals("1111:1111-2222:1111:1111:1111:1111:1111:1111", ipCreeEventEntity.getIp());
        Assertions.assertEquals(IpType.IPV6, ipCreeEventEntity.getTypeIp());
        Assertions.assertEquals("range", ipCreeEventEntity.getTypeAcces());
    }

    @Test
    @DisplayName("test convertisseur IpAjoutee / IpCreeEvent avec exceptions")
    void testIpAjouteeExceptions() {
        Ipv4AjouteeWebDto ipv4 = new Ipv4AjouteeWebDto();

        Assertions.assertThrows(MappingException.class, () -> mapper.map(ipv4, IpCreeEventEntity.class));

        Ipv6AjouteeWebDto ipv6 = new Ipv6AjouteeWebDto();

        Assertions.assertThrows(MappingException.class, () -> mapper.map(ipv6, IpCreeEventEntity.class));
    }

    @Test
    @DisplayName("test convertisseur IpEntity / IpSupprimeeEvent")
    void testIpEntityIpSupprimeeEvent() throws IpException {
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etab = new EtablissementEntity("nomTest", "111111111", contact);
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ipv4 = new IpV4("1.1.1.1", "test", statut);
        ipv4.setEtablissement(etab);

        IpSupprimeeEventEntity event = mapper.map(ipv4, IpSupprimeeEventEntity.class);

        Assertions.assertEquals("111111111", event.getSiren());
        Assertions.assertEquals("1.1.1.1", event.getIp());
        Assertions.assertEquals("ip", event.getTypeAcces());
        Assertions.assertEquals(IpType.IPV4, event.getTypeIp());

        IpEntity ipv6 = new IpV6("1111:1111:1111:1111:1111:1111:1111:1111", "test", statut);
        ipv6.setEtablissement(etab);

        IpSupprimeeEventEntity event2 = mapper.map(ipv6, IpSupprimeeEventEntity.class);

        Assertions.assertEquals("111111111", event2.getSiren());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", event2.getIp());
        Assertions.assertEquals("ip", event2.getTypeAcces());
        Assertions.assertEquals(IpType.IPV6, event2.getTypeIp());
    }

    @Test
    @DisplayName("test convertisseur IpEntity / IpRejeteeEvent")
    void testIpEntityIpRejeteeEvent() throws IpException {
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etab = new EtablissementEntity("nomTest", "111111111", contact);
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ipv4 = new IpV4("1.1.1.1", "test", statut);
        ipv4.setEtablissement(etab);

        IpRejeteeEventEntity event = mapper.map(ipv4, IpRejeteeEventEntity.class);

        Assertions.assertEquals("111111111", event.getSiren());
        Assertions.assertEquals("1.1.1.1", event.getIp());
        Assertions.assertEquals("ip", event.getTypeAcces());
        Assertions.assertEquals(IpType.IPV4, event.getTypeIp());

        IpEntity ipv6 = new IpV6("1111:1111:1111:1111:1111:1111:1111:1111", "test", statut);
        ipv6.setEtablissement(etab);

        IpRejeteeEventEntity event2 = mapper.map(ipv6, IpRejeteeEventEntity.class);

        Assertions.assertEquals("111111111", event2.getSiren());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", event2.getIp());
        Assertions.assertEquals("ip", event2.getTypeAcces());
        Assertions.assertEquals(IpType.IPV6, event2.getTypeIp());
    }

    @Test
    @DisplayName("test convertisseur IpEntity / IpValideeEvent")
    void testIpEntityIpValideeEvent() throws IpException {
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etab = new EtablissementEntity("nomTest", "111111111", contact);
        StatutIpEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ipv4 = new IpV4("1.1.1.1", "test", statut);
        ipv4.setEtablissement(etab);

        IpValideeEventEntity event = mapper.map(ipv4, IpValideeEventEntity.class);

        Assertions.assertEquals("111111111", event.getSiren());
        Assertions.assertEquals("1.1.1.1", event.getIp());
        Assertions.assertEquals("ip", event.getTypeAcces());
        Assertions.assertEquals(IpType.IPV4, event.getTypeIp());

        IpEntity ipv6 = new IpV6("1111:1111:1111:1111:1111:1111:1111:1111", "test", statut);
        ipv6.setEtablissement(etab);

        IpValideeEventEntity event2 = mapper.map(ipv6, IpValideeEventEntity.class);

        Assertions.assertEquals("111111111", event2.getSiren());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", event2.getIp());
        Assertions.assertEquals("ip", event2.getTypeAcces());
        Assertions.assertEquals(IpType.IPV6, event2.getTypeIp());
    }
}