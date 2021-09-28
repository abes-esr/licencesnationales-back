package fr.abes.licencesnationales.web.converter.ip;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpModifieeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import fr.abes.licencesnationales.web.dto.ip.cree.Ipv4AjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.cree.Ipv6AjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.modifie.IpModifieeAdminWebDto;
import fr.abes.licencesnationales.web.dto.ip.modifie.IpModifieeUserWebDto;
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
    @DisplayName("test convertisseur IpModifie / IpModifieEvent")
    void testIpModifie() {
        IpModifieeUserWebDto ipv4User = new IpModifieeUserWebDto();
        ipv4User.setTypeIp("IPV4");
        ipv4User.setIp("1.1.1.1");
        ipv4User.setCommentaires("test");

        IpModifieeEventEntity ipModifieeEventEntity = mapper.map(ipv4User, IpModifieeEventEntity.class);

        Assertions.assertEquals("test", ipModifieeEventEntity.getCommentaires());
        Assertions.assertEquals("1.1.1.1", ipModifieeEventEntity.getIp());
        Assertions.assertEquals(IpType.IPV4, ipModifieeEventEntity.getTypeIp());
        Assertions.assertEquals("ip", ipModifieeEventEntity.getTypeAcces());

        IpModifieeAdminWebDto ipv4Admin = new IpModifieeAdminWebDto();
        ipv4Admin.setTypeIp("IPV6");
        ipv4Admin.setIp("1111:1111:1111:1111:1111:1111:1111:1111");
        ipv4Admin.setCommentaires("test");
        ipv4Admin.setStatut("En validation");

        ipModifieeEventEntity = mapper.map(ipv4Admin, IpModifieeEventEntity.class);

        Assertions.assertEquals("test", ipModifieeEventEntity.getCommentaires());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", ipModifieeEventEntity.getIp());
        Assertions.assertEquals(IpType.IPV6, ipModifieeEventEntity.getTypeIp());
        Assertions.assertEquals("ip", ipModifieeEventEntity.getTypeAcces());
        Assertions.assertEquals("En validation", ipModifieeEventEntity.getStatut());

    }

    @Test
    @DisplayName("test convertisseur IpModifie / IpModifieEvent avec exceptions")
    void testIpModifieExceptions() {
        IpModifieeUserWebDto ipv4User2 = new IpModifieeUserWebDto();

        Assertions.assertThrows(MappingException.class, () -> mapper.map(ipv4User2, IpModifieeEventEntity.class));

        IpModifieeUserWebDto ipv4User3 = new IpModifieeUserWebDto();
        ipv4User3.setIp("1.1.1.1");

        Assertions.assertThrows(MappingException.class, () -> mapper.map(ipv4User3, IpModifieeEventEntity.class));

        IpModifieeAdminWebDto ipv4Admin2 = new IpModifieeAdminWebDto();

        Assertions.assertThrows(MappingException.class, () -> mapper.map(ipv4Admin2, IpModifieeEventEntity.class));

        IpModifieeAdminWebDto ipv4Admin3 = new IpModifieeAdminWebDto();
        ipv4User3.setIp("1.1.1.1");

        Assertions.assertThrows(MappingException.class, () -> mapper.map(ipv4Admin3, IpModifieeEventEntity.class));

    }
}