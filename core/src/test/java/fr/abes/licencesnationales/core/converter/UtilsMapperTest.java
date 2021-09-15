package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.converter.editeur.EditeurEntityConverter;
import fr.abes.licencesnationales.core.converter.etablissement.EtablissementEntityConverter;
import fr.abes.licencesnationales.core.converter.ip.IpEntityConverter;
import fr.abes.licencesnationales.core.converter.ip.Ipv4RangeConverter;
import fr.abes.licencesnationales.core.converter.ip.Ipv6RangeConverter;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.repository.TypeEtablissementRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, EtablissementEntityConverter.class, IpEntityConverter.class, Ipv4RangeConverter.class, Ipv6RangeConverter.class, EditeurEntityConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @InjectMocks
    private EtablissementEntityConverter etablissementEntityConverter;

    @MockBean
    private TypeEtablissementRepository repository;

    @Test
    @DisplayName("test nouvelEditeur 1CC 0CT")
    public void testMapperEditeurCreeEventVersEditeurEntity1() {
        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this);

        editeurCreeEvent.setNomEditeur("nomEditeur");
        editeurCreeEvent.setAdresseEditeur("adressePostale");
        editeurCreeEvent.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeEvent.setGroupesEtabRelies(groupesEtabRelies);

        ContactCommercialEditeurEntity contactComm = new ContactCommercialEditeurEntity("nomCCA", "prenomCCA", "mail@CCA.com");
        Set<ContactCommercialEditeurEntity> setComm = new HashSet<>();
        setComm.add(contactComm);
        editeurCreeEvent.setListeContactCommercialEditeur(setComm);

        ContactTechniqueEditeurEntity contactTech = new ContactTechniqueEditeurEntity("testNomTech", "testPrenomTech", "test@test.tech");
        Set<ContactTechniqueEditeurEntity> setTech = new HashSet<>();
        setTech.add(contactTech);
        editeurCreeEvent.setListeContactTechniqueEditeur(setTech);

        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("nomEditeur", entity.getNomEditeur());
        Assertions.assertEquals("adressePostale", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());
        Assertions.assertEquals(2, entity.getGroupesEtabRelies().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertEquals("nomCCA", commercialEntity.getNomContact());
        Assertions.assertEquals("prenomCCA", commercialEntity.getPrenomContact());
        Assertions.assertEquals("mail@CCA.com", commercialEntity.getMailContact());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertEquals("testNomTech", techniqueEntity.getNomContact());
        Assertions.assertEquals("testPrenomTech", techniqueEntity.getPrenomContact());
        Assertions.assertEquals("test@test.tech", techniqueEntity.getMailContact());
    }

    @Test
    @DisplayName("test nouvelEditeur 2CC 2CT")
    public void testMapperEditeurCreeEventVersEditeurEntity2() {
        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this);

        editeurCreeEvent.setNomEditeur("nomEditeur");
        editeurCreeEvent.setAdresseEditeur("adressePostale");
        editeurCreeEvent.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeEvent.setGroupesEtabRelies(groupesEtabRelies);

        ContactCommercialEditeurEntity cc1 = new ContactCommercialEditeurEntity("nomCCA", "prenomCCA", "mail@CCA.com");
        ContactCommercialEditeurEntity cc2 = new ContactCommercialEditeurEntity("nomCCB", "prenomCCB", "mail@CCB.com");

        Set<ContactCommercialEditeurEntity> set = new HashSet<>();
        set.add(cc1);
        set.add(cc2);
        editeurCreeEvent.setListeContactCommercialEditeur(set);

        ContactTechniqueEditeurEntity ct1 = new ContactTechniqueEditeurEntity("nomCTA", "prenomCTA", "mail@CTA.com");
        ContactTechniqueEditeurEntity ct2 = new ContactTechniqueEditeurEntity("nomCTB", "prenomCTB", "mail@CTB.com");

        Set<ContactTechniqueEditeurEntity> set2 = new HashSet<>();
        set2.add(ct1);
        set2.add(ct2);
        editeurCreeEvent.setListeContactTechniqueEditeur(set2);


        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("nomEditeur", entity.getNomEditeur());
        Assertions.assertEquals("adressePostale", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());
        Assertions.assertEquals(2, entity.getGroupesEtabRelies().size());

        Assertions.assertEquals(2, entity.getContactCommercialEditeurEntities().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertTrue(isValidNomC(commercialEntity.getNomContact()));
        Assertions.assertTrue(isValidPrenomC(commercialEntity.getPrenomContact()));
        Assertions.assertTrue(isValidMailC(commercialEntity.getMailContact()));

        Assertions.assertEquals(2, entity.getContactTechniqueEditeurEntities().size());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertTrue(isValidNomT(techniqueEntity.getNomContact()));
        Assertions.assertTrue(isValidPrenomT(techniqueEntity.getPrenomContact()));
        Assertions.assertTrue(isValidMailT(techniqueEntity.getMailContact()));

    }
    public Boolean isValidNomC(String s) {
        if(s.equals("nomCCA") || s.equals("nomCCB")) {
            return true;
        }
        return false;
    }
    public Boolean isValidNomT(String s) {
        if(s.equals("nomCTA") || s.equals("nomCTB")) {
            return true;
        }
        return false;
    }
    public Boolean isValidPrenomC(String s) {
        if(s.equals("prenomCCA") || s.equals("prenomCCB")) {
            return true;
        }
        return false;
    }
    public Boolean isValidPrenomT(String s) {
        if(s.equals("prenomCTA") || s.equals("prenomCTB")) {
            return true;
        }
        return false;
    }
    public Boolean isValidMailC(String s) {
        if(s.equals("mail@CCA.com") || s.equals("mail@CCB.com")) {
            return true;
        }
        return false;
    }
    public Boolean isValidMailT(String s) {
        if(s.equals("mail@CTA.com") || s.equals("mail@CTB.com")) {
            return true;
        }
        return false;
    }

    @Test
    @DisplayName("test mapper établissement modifié")
    public void testMapperEtablissementModifieEvent() {
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
        Mockito.when(repository.findFirstByLibelle(Mockito.anyString())).thenReturn(java.util.Optional.of(new TypeEtablissementEntity(1, "testType")));
        EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this);

        etablissementCreeEvent.setNom("testNom");
        etablissementCreeEvent.setTypeEtablissement("testType");
        etablissementCreeEvent.setSiren("123456789");
        etablissementCreeEvent.setIdAbes("1234");
        etablissementCreeEvent.setNomContact("nomContactTest");
        etablissementCreeEvent.setPrenomContact("prenomContactTest");
        etablissementCreeEvent.setMailContact("mail@test.com");
        etablissementCreeEvent.setMotDePasse("passwordtest");
        etablissementCreeEvent.setTelephoneContact("0000000000");
        etablissementCreeEvent.setAdresseContact("adresseTest");
        etablissementCreeEvent.setBoitePostaleContact("BPTest");
        etablissementCreeEvent.setCodePostalContact("00000");
        etablissementCreeEvent.setCedexContact("cedexTest");
        etablissementCreeEvent.setVilleContact("villeTest");
        etablissementCreeEvent.setRoleContact("roleTest");

        EtablissementEntity entity = utilsMapper.map(etablissementCreeEvent, EtablissementEntity.class);

        Assertions.assertEquals("testType", entity.getTypeEtablissement().getLibelle());
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
    @DisplayName("test mapper etablissement créé avec type inconnu")
    public void testMapperEtablissementEventCreeWithUnknownType() {
        Mockito.when(repository.findFirstByLibelle(Mockito.anyString())).thenThrow(new MappingException(Lists.newArrayList(new ErrorMessage("Type d'établissement inconnu"))));
        EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this);

        etablissementCreeEvent.setNom("testNom");
        etablissementCreeEvent.setTypeEtablissement("testType");
        etablissementCreeEvent.setSiren("123456789");
        etablissementCreeEvent.setIdAbes("1234");
        etablissementCreeEvent.setNomContact("nomContactTest");
        etablissementCreeEvent.setPrenomContact("prenomContactTest");
        etablissementCreeEvent.setMailContact("mail@test.com");
        etablissementCreeEvent.setMotDePasse("passwordtest");
        etablissementCreeEvent.setTelephoneContact("0000000000");
        etablissementCreeEvent.setAdresseContact("adresseTest");
        etablissementCreeEvent.setBoitePostaleContact("BPTest");
        etablissementCreeEvent.setCodePostalContact("00000");
        etablissementCreeEvent.setCedexContact("cedexTest");
        etablissementCreeEvent.setVilleContact("villeTest");
        etablissementCreeEvent.setRoleContact("roleTest");

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissementCreeEvent, EtablissementEntity.class));

    }

    @Test
    @DisplayName("test mapper IP créée")
    public void testMapperIpEventCree() {
        IpAjouteeEvent ip = new IpAjouteeEvent(this);
        ip.setSiren("123456789");
        ip.setTypeIp(IpType.IPV4);
        ip.setTypeAcces("ip");
        ip.setIp("200.200.200.200");

        IpEntity ipEntity = utilsMapper.map(ip, IpEntity.class);

        Assertions.assertEquals(ipEntity.getClass(), IpV4.class);
        Assertions.assertEquals("123456789", ip.getSiren());

    }

}
