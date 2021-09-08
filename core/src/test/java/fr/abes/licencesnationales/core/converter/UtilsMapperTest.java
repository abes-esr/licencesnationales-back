package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.contact.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.contact.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, EntityConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @Test
    @DisplayName("test nouvelEditeur 1CC 0CT")
    public void testMapperEditeurCreeDtoVersEditeurEntity1() {
        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this);

        editeurCreeEvent.setNomEditeur("nomEditeur");
        editeurCreeEvent.setAdresseEditeur("adressePostale");
        editeurCreeEvent.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeEvent.setGroupesEtabRelies(groupesEtabRelies);

        ContactCommercialEditeurDto contactComm = new ContactCommercialEditeurDto("nomCCA", "prenomCCA", "mail@CCA.com");
        Set<ContactCommercialEditeurDto> setComm = new HashSet<>();
        setComm.add(contactComm);
        editeurCreeEvent.setListeContactCommercialEditeur(setComm);

        ContactTechniqueEditeurDto contactTech = new ContactTechniqueEditeurDto("testNomTech", "testPrenomTech", "test@test.tech");
        Set<ContactTechniqueEditeurDto> setTech = new HashSet<>();
        setTech.add(contactTech);
        editeurCreeEvent.setListeContactTechniqueEditeur(setTech);

        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("nomEditeur", entity.getNomEditeur());
        Assertions.assertEquals("adressePostale", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());
        Assertions.assertEquals(2, entity.getGroupesEtabRelies().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertEquals("nomCCA", commercialEntity.getNomContactCommercial());
        Assertions.assertEquals("prenomCCA", commercialEntity.getPrenomContactCommercial());
        Assertions.assertEquals("mail@CCA.com", commercialEntity.getMailContactCommercial());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertEquals("testNomTech", techniqueEntity.getNomContactTechnique());
        Assertions.assertEquals("testPrenomTech", techniqueEntity.getPrenomContactTechnique());
        Assertions.assertEquals("test@test.tech", techniqueEntity.getMailContactTechnique());
    }

    @Test
    @DisplayName("test nouvelEditeur 2CC 2CT")
    public void testMapperEditeurCreeDtoVersEditeurEntity2() {
        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this);

        editeurCreeEvent.setNomEditeur("nomEditeur");
        editeurCreeEvent.setAdresseEditeur("adressePostale");
        editeurCreeEvent.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeEvent.setGroupesEtabRelies(groupesEtabRelies);

        ContactCommercialEditeurDto cc1 = new ContactCommercialEditeurDto("nomCCA", "prenomCCA", "mail@CCA.com");
        ContactCommercialEditeurDto cc2 = new ContactCommercialEditeurDto("nomCCB", "prenomCCB", "mail@CCB.com");

        Set<ContactCommercialEditeurDto> set = new HashSet<>();
        set.add(cc1);
        set.add(cc2);
        editeurCreeEvent.setListeContactCommercialEditeur(set);

        ContactTechniqueEditeurDto ct1 = new ContactTechniqueEditeurDto("nomCTA", "prenomCTA", "mail@CTA.com");
        ContactTechniqueEditeurDto ct2 = new ContactTechniqueEditeurDto("nomCTB", "prenomCTB", "mail@CTB.com");

        Set<ContactTechniqueEditeurDto> set2 = new HashSet<>();
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
        Assertions.assertTrue(isValidNomC(commercialEntity.getNomContactCommercial()));
        Assertions.assertTrue(isValidPrenomC(commercialEntity.getPrenomContactCommercial()));
        Assertions.assertTrue(isValidMailC(commercialEntity.getMailContactCommercial()));

        Assertions.assertEquals(2, entity.getContactTechniqueEditeurEntities().size());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertTrue(isValidNomT(techniqueEntity.getNomContactTechnique()));
        Assertions.assertTrue(isValidPrenomT(techniqueEntity.getPrenomContactTechnique()));
        Assertions.assertTrue(isValidMailT(techniqueEntity.getMailContactTechnique()));

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
    @DisplayName("test mapper editeur fusionné")
    public void testMapperEditeurFusionneEvent() {
        EditeurFusionneEvent editeurFusionneEvent = new EditeurFusionneEvent(this);
        List<Long> listeId = new ArrayList<>();
        listeId.add(1L);
        listeId.add(2L);
        editeurFusionneEvent.setIdEditeurFusionnes(listeId);
        editeurFusionneEvent.setNomEditeur("testNom");
        editeurFusionneEvent.setIdentifiantEditeur("123456789");
        editeurFusionneEvent.setAdresseEditeur("testAdresse");

        List<String> listeGroupeEtab = new ArrayList<>();
        listeGroupeEtab.add("testGroup1");
        listeGroupeEtab.add("testGroup2");
        editeurFusionneEvent.setGroupesEtabRelies(listeGroupeEtab);

        Set<ContactTechniqueEditeurDto> contactTechniqueEditeurDtoSet = new HashSet<>();
        contactTechniqueEditeurDtoSet.add(new ContactTechniqueEditeurDto("testNomCT", "testPrenomCT", "test@test.CT"));
        editeurFusionneEvent.setListeContactTechniqueEditeur(contactTechniqueEditeurDtoSet);

        Set<ContactCommercialEditeurDto> contactCommercialEditeurDtos = new HashSet<>();
        contactCommercialEditeurDtos.add(new ContactCommercialEditeurDto("testNomCC", "testPrenomCC", "test@test.CC"));
        editeurFusionneEvent.setListeContactCommercialEditeur(contactCommercialEditeurDtos);

        EditeurEntity entity = utilsMapper.map(editeurFusionneEvent, EditeurEntity.class);

        Assertions.assertEquals("testNom", entity.getNomEditeur());
        Assertions.assertEquals("123456789", entity.getIdentifiantEditeur());
        Assertions.assertEquals("testAdresse", entity.getAdresseEditeur());
        Assertions.assertEquals("testGroup1", entity.getGroupesEtabRelies().get(0));
        Assertions.assertEquals("testGroup2", entity.getGroupesEtabRelies().get(1));
        ContactTechniqueEditeurEntity ct = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertEquals("testNomCT", ct.getNomContactTechnique());
        Assertions.assertEquals("testPrenomCT", ct.getPrenomContactTechnique());
        Assertions.assertEquals("test@test.CT", ct.getMailContactTechnique());
        ContactCommercialEditeurEntity cc = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertEquals("testNomCC", cc.getNomContactCommercial());
        Assertions.assertEquals("testPrenomCC", cc.getPrenomContactCommercial());
        Assertions.assertEquals("test@test.CC", cc.getMailContactCommercial());

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
        IpAjouteeEvent ipAjouteeEvent = new IpAjouteeEvent(this, "123456789", "IPV4", "ip", "127.0.0.1", "testCommentaire");

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
