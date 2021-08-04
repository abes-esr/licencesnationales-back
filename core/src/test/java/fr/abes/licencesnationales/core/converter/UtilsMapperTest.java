package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, EntityConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @Test
    @DisplayName("test création Event Editeur")
    public void testMapperEditeurEventCree() {
        EditeurCreeDto editeurCreeDto = new EditeurCreeDto();
        editeurCreeDto.setNomEditeur("testNom");
        editeurCreeDto.setAdresseEditeur("testAdresse");
        editeurCreeDto.setIdentifiantEditeur("123456");
        editeurCreeDto.setGroupesEtabRelies(Arrays.asList("1", "2"));

        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeurCreeDto);

        ContactCommercialEditeurDto commercial = new ContactCommercialEditeurDto("testNom", "testPrenom", "test@test.com");
        Set<ContactCommercialEditeurDto> set = new HashSet<>();
        set.add(commercial);
        editeurCreeDto.setListeContactCommercialEditeurDto(set);

        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("testNom", entity.getNomEditeur());
        Assertions.assertEquals("testAdresse", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());
        Assertions.assertEquals(2, entity.getGroupesEtabRelies().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertEquals("testNom", commercialEntity.getNomContactCommercial());
    }

    @Test
    @DisplayName("test mapper établissement")
    public void testMapperEtablissementEvent() {
        EtablissementDto etablissement = new EtablissementDto();
        etablissement.setNom("testNom");
        etablissement.setTypeEtablissement("testType");
        etablissement.setSiren("123456789");
        etablissement.setIdAbes("1234");
        etablissement.setNomContact("nomContactTest");
        etablissement.setPrenomContact("prenomContactTest");
        etablissement.setMailContact("mail@test.com");
        etablissement.setMotDePasse("passwordtest");
        etablissement.setTelephoneContact("0000000000");
        etablissement.setAdresseContact("adresseTest");
        etablissement.setBoitePostaleContact("BPTest");
        etablissement.setCodePostalContact("00000");
        etablissement.setCedexContact("cedexTest");
        etablissement.setVilleContact("villeTest");
        etablissement.setRoleContact("roleTest");

        EtablissementEntity entity = utilsMapper.map(etablissement, EtablissementEntity.class);

        Assertions.assertEquals("testType", entity.getTypeEtablissement());
        Assertions.assertEquals("123456789", entity.getSiren());
        Assertions.assertEquals("1234", entity.getIdAbes());
        Assertions.assertEquals("nomContactTest",entity.getContact().getNom());
        Assertions.assertEquals("prenomContactTest", entity.getContact().getPrenom());
        Assertions.assertEquals("mail@test.com", entity.getContact().getMail());
        Assertions.assertEquals("0000000000", entity.getContact().getTelephone());
        Assertions.assertEquals("adresseTest", entity.getContact().getAdresse());
        Assertions.assertEquals("BPTest", entity.getContact().getBoitePostale());
        Assertions.assertEquals("00000", entity.getContact().getCodePostal());
        Assertions.assertEquals("cedexTest", entity.getContact().getCedex());
        Assertions.assertEquals("villeTest", entity.getContact().getVille());
        Assertions.assertEquals("roleTest", entity.getContact().getRole());
        Assertions.assertEquals("testNom", entity.getName());
    }

    @Test
    @DisplayName("test mapper établissement modifié")
    public void testMapperEtablissementEventModifie() {
        EtablissementModifieEvent etablissementModifieEvent = new EtablissementModifieEvent(this, "123456789", "nomContactTest", "prenomContactTest", "mail@test.com", "0000000000", "adresseTest", "BPTest", "00000", "villeTest", "cedexTest");

        ContactEntity entity = utilsMapper.map(etablissementModifieEvent, ContactEntity.class);

        Assertions.assertEquals("nomContactTest",entity.getNom());
        Assertions.assertEquals("prenomContactTest", entity.getPrenom());
        Assertions.assertEquals("mail@test.com", entity.getMail());
        Assertions.assertEquals("0000000000", entity.getTelephone());
        Assertions.assertEquals("adresseTest", entity.getAdresse());
        Assertions.assertEquals("BPTest", entity.getBoitePostale());
        Assertions.assertEquals("00000", entity.getCodePostal());
        Assertions.assertEquals("cedexTest", entity.getCedex());
        Assertions.assertEquals("villeTest", entity.getVille());
    }

    @Test
    @DisplayName("test mapper etablissement créé")
    public void testMapperEtablissementEventCree() {
        EtablissementCreeDto etablissement = new EtablissementCreeDto();
        EtablissementDto etablissementDto = new EtablissementDto();
        etablissementDto.setNom("testNom");
        etablissementDto.setTypeEtablissement("testType");
        etablissementDto.setSiren("123456789");
        etablissementDto.setIdAbes("1234");
        etablissementDto.setNomContact("nomContactTest");
        etablissementDto.setPrenomContact("prenomContactTest");
        etablissementDto.setMailContact("mail@test.com");
        etablissementDto.setMotDePasse("passwordtest");
        etablissementDto.setTelephoneContact("0000000000");
        etablissementDto.setAdresseContact("adresseTest");
        etablissementDto.setBoitePostaleContact("BPTest");
        etablissementDto.setCodePostalContact("00000");
        etablissementDto.setCedexContact("cedexTest");
        etablissementDto.setVilleContact("villeTest");
        etablissementDto.setRoleContact("roleTest");

        etablissement.setEtablissementDTO(etablissementDto);

        EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this, etablissement);

        EtablissementEntity entity = utilsMapper.map(etablissementCreeEvent, EtablissementEntity.class);

        Assertions.assertEquals("testType", entity.getTypeEtablissement());
        Assertions.assertEquals("123456789", entity.getSiren());
        Assertions.assertEquals("1234", entity.getIdAbes());
        Assertions.assertEquals("nomContactTest",entity.getContact().getNom());
        Assertions.assertEquals("prenomContactTest", entity.getContact().getPrenom());
        Assertions.assertEquals("passwordtest", entity.getContact().getMotDePasse());
        Assertions.assertEquals("mail@test.com", entity.getContact().getMail());
        Assertions.assertEquals("0000000000", entity.getContact().getTelephone());
        Assertions.assertEquals("adresseTest", entity.getContact().getAdresse());
        Assertions.assertEquals("BPTest", entity.getContact().getBoitePostale());
        Assertions.assertEquals("00000", entity.getContact().getCodePostal());
        Assertions.assertEquals("cedexTest", entity.getContact().getCedex());
        Assertions.assertEquals("villeTest", entity.getContact().getVille());
        Assertions.assertEquals("roleTest", entity.getContact().getRole());
        Assertions.assertEquals("testNom", entity.getName());
    }

    @Test
    @DisplayName("test mapper IP créée")
    public void testMapperIpEventAJoutee() {
        IpAjouteeDto ipAjouteeDto = new IpAjouteeDto();
        ipAjouteeDto.setSiren("123456789");
        ipAjouteeDto.setCommentaires("testCommentaire");
        ipAjouteeDto.setIp("127.0.0.1");
        ipAjouteeDto.setTypeIp("IPV4");
        ipAjouteeDto.setTypeAcces("ip");

        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this, ipAjouteeDto);

        IpEntity ipEntity = utilsMapper.map(ipAjouteeEvent, IpEntity.class);

        Assertions.assertEquals("testCommentaire", ipEntity.getCommentaires());
        Assertions.assertEquals("127.0.0.1", ipEntity.getIp());
        Assertions.assertEquals("IPV4", ipEntity.getTypeIp());
        Assertions.assertEquals("ip", ipEntity.getTypeAcces());
    }

    @Test
    @DisplayName("test mapper IP modifiée")
    public void testMapperIpEventModifiee() {
        IpModifieeEvent ipModifieeEvent = new IpModifieeEvent(this,"123456789", "127.0.0.1", true, "ip", "IPV4", "testCommentaire");

        IpEntity ipEntity = utilsMapper.map(ipModifieeEvent, IpEntity.class);

        Assertions.assertEquals("testCommentaire", ipEntity.getCommentaires());
        Assertions.assertEquals("127.0.0.1", ipEntity.getIp());
        Assertions.assertEquals("IPV4", ipEntity.getTypeIp());
        Assertions.assertEquals("ip", ipEntity.getTypeAcces());
    }
}
