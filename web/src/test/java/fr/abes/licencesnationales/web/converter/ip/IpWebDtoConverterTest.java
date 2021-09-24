package fr.abes.licencesnationales.web.converter.ip;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.web.converter.etablissement.EtablissementWebDtoConverter;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, IpWebDtoConverter.class, ObjectMapper.class})
class IpWebDtoConverterTest {
    @Autowired
    private UtilsMapper mapper;

    @Test
    @DisplayName("Test convertisseur IpV4 / IpWebDto")
    void testEntityIpV4WebDto() throws IpException {
        IpWebDto dto;
        StatutEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV4(1, "1.1.1.1", "test");
        ip.setStatut((StatutIpEntity) statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1.1.1.1", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IpV4", dto.getTypeIp());
        Assertions.assertEquals("ip", dto.getTypeAcces());
    }

    @Test
    @DisplayName("test convertisseur range IpV4 / IpWebDto")
    void testEntityIpRangeV4WebDto() throws IpException {
        IpWebDto dto;
        StatutEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV4(1, "1.1-10.1.1", "test");
        ip.setStatut((StatutIpEntity) statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1.1-10.1.1", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IpV4", dto.getTypeIp());
        Assertions.assertEquals("range", dto.getTypeAcces());

    }

    @Test
    @DisplayName("Test convertisseur IpV6 / IpWebDto")
    void testEntityIpV6WebDto() throws IpException {
        IpWebDto dto;
        StatutEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV6(1, "1111:1111:1111:1111:1111:1111:1111:1111", "test");
        ip.setStatut((StatutIpEntity) statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IpV6", dto.getTypeIp());
        Assertions.assertEquals("ip", dto.getTypeAcces());
    }

    @Test
    @DisplayName("Test convertisseur range IpV6 / IpWebDto")
    void testEntityIpRangeV6WebDto() throws IpException {
        IpWebDto dto;
        StatutEntity statut = new StatutIpEntity(1, "Valide");
        IpEntity ip = new IpV6(1, "1111:1111-2222:1111:1111:1111-2222:1111:1111:1111", "test");
        ip.setStatut((StatutIpEntity) statut);
        dto = mapper.map(ip, IpWebDto.class);
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("1111:1111-2222:1111:1111:1111-2222:1111:1111:1111", dto.getIp());
        Assertions.assertEquals("Valide", dto.getStatut());
        Assertions.assertEquals("test", dto.getCommentaires());
        Assertions.assertEquals("IpV6", dto.getTypeIp());
        Assertions.assertEquals("range", dto.getTypeAcces());
    }
}