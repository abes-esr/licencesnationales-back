package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.entities.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.ContactEntity;
import fr.abes.licencesnationales.core.entities.EditeurEntity;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UtilsMapper.class, EntityConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @Test
    @DisplayName("test création Event Editeur")
    public void testMapperEditeurCreeEvent() {
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

        Assert.assertEquals("testNom", entity.getNomEditeur());
        Assert.assertEquals("testAdresse", entity.getAdresseEditeur());
        Assert.assertEquals("123456", entity.getIdentifiantEditeur());
        Assert.assertEquals(2, entity.getGroupesEtabRelies().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assert.assertEquals("testNom", commercialEntity.getNomContactCommercial());
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

        Assert.assertEquals("testType", entity.getTypeEtablissement());
        Assert.assertEquals("123456789", entity.getSiren());
        Assert.assertEquals("1234", entity.getIdAbes());
        Assert.assertEquals("nomContactTest",entity.getContact().getNom());
        Assert.assertEquals("prenomContactTest", entity.getContact().getPrenom());
        Assert.assertEquals("mail@test.com", entity.getContact().getMail());
        Assert.assertEquals("0000000000", entity.getContact().getTelephone());
        Assert.assertEquals("adresseTest", entity.getContact().getAdresse());
        Assert.assertEquals("BPTest", entity.getContact().getBoitePostale());
        Assert.assertEquals("00000", entity.getContact().getCodePostal());
        Assert.assertEquals("cedexTest", entity.getContact().getCedex());
        Assert.assertEquals("villeTest", entity.getContact().getVille());
        Assert.assertEquals("roleTest", entity.getContact().getRole());
        Assert.assertEquals("testNom", entity.getName());
    }

    @Test
    @DisplayName("test mapper établissement modifié")
    public void testMapperEtablissementEventModifie() {
        EtablissementModifieEvent etablissementModifieEvent = new EtablissementModifieEvent(this, "123456789", "nomContactTest", "prenomContactTest", "mail@test.com", "0000000000", "adresseTest", "BPTest", "00000", "villeTest", "cedexTest");

        ContactEntity entity = utilsMapper.map(etablissementModifieEvent, ContactEntity.class);

        Assert.assertEquals("nomContactTest",entity.getNom());
        Assert.assertEquals("prenomContactTest", entity.getPrenom());
        Assert.assertEquals("mail@test.com", entity.getMail());
        Assert.assertEquals("0000000000", entity.getTelephone());
        Assert.assertEquals("adresseTest", entity.getAdresse());
        Assert.assertEquals("BPTest", entity.getBoitePostale());
        Assert.assertEquals("00000", entity.getCodePostal());
        Assert.assertEquals("cedexTest", entity.getCedex());
        Assert.assertEquals("villeTest", entity.getVille());
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

        Assert.assertEquals("testType", entity.getTypeEtablissement());
        Assert.assertEquals("123456789", entity.getSiren());
        Assert.assertEquals("1234", entity.getIdAbes());
        Assert.assertEquals("nomContactTest",entity.getContact().getNom());
        Assert.assertEquals("prenomContactTest", entity.getContact().getPrenom());
        Assert.assertEquals("passwordtest", entity.getContact().getMotDePasse());
        Assert.assertEquals("mail@test.com", entity.getContact().getMail());
        Assert.assertEquals("0000000000", entity.getContact().getTelephone());
        Assert.assertEquals("adresseTest", entity.getContact().getAdresse());
        Assert.assertEquals("BPTest", entity.getContact().getBoitePostale());
        Assert.assertEquals("00000", entity.getContact().getCodePostal());
        Assert.assertEquals("cedexTest", entity.getContact().getCedex());
        Assert.assertEquals("villeTest", entity.getContact().getVille());
        Assert.assertEquals("roleTest", entity.getContact().getRole());
        Assert.assertEquals("testNom", entity.getName());
    }
}
