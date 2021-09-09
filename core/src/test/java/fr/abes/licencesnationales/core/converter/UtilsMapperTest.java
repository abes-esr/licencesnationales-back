package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurSupprimeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurSupprimeEvent;
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

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, EntityConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;

    /*
    1 CC 0 CT
    {
    "nomEditeur":"hhhhhh",
    "identifiantEditeur":"",
    "groupesEtabRelies":[],
    "adresseEditeur":"dddddddd",
    "listeContactCommercialEditeurDTO":[{"nomContactCommercial":"dd","prenomContactCommercial":"dd","mailContactCommercial":"dd@l.m"}],
    "listeContactTechniqueEditeurDTO":[]}

    2 CC 2 CT
    {
        "nomEditeur":"nomEditeur",
        "identifiantEditeur":"123456",
        "groupesEtabRelies":["EPIC/EPST","Ecoles d'ingénieurs"],
        "adresseEditeur":"adressePostale",
        "listeContactCommercialEditeurDTO":[{"nomContactCommercial":"nomCCA","prenomContactCommercial":"prenomCCA","mailContactCommercial":"mail@CCA.com"},{"nomContactCommercial":"nomCCB","prenomContactCommercial":"prenomCCB","mailContactCommercial":"mail@CCB.com"}],
        "listeContactTechniqueEditeurDTO":[{"nomContactTechnique":"nomCTA","prenomContactTechnique":"prenomCTA","mailContactTechnique":"mail@CTA.com"},{"nomContactTechnique":"nomCTB","prenomContactTechnique":"prenomCTB","mailContactTechnique":"mail@CTB.com"}]
    }

     */

    @Test
    @DisplayName("test nouvelEditeur 1CC 0CT")
    public void testMapperEditeurCreeDtoVersEditeurEntity1() {
        EditeurCreeDto editeurCreeDto = new EditeurCreeDto();
        editeurCreeDto.setNomEditeur("nomEditeur");
        editeurCreeDto.setAdresseEditeur("adressePostale");
        editeurCreeDto.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeDto.setGroupesEtabRelies(groupesEtabRelies);

        //1 CC
        ContactCommercialEditeurDto cc1 = new ContactCommercialEditeurDto("nomCCA", "prenomCCA", "mail@CCA.com");
        Set<ContactCommercialEditeurDto> set = new HashSet<>();
        set.add(cc1);
        editeurCreeDto.setListeContactCommercialEditeurDto(set);

        //0 CT
        Set<ContactTechniqueEditeurDto> set2 = new HashSet<>();
        editeurCreeDto.setListeContactTechniqueEditeurDto(set2);

        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeurCreeDto);


        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("nomEditeur", entity.getNomEditeur());
        Assertions.assertEquals("adressePostale", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());
        Assertions.assertEquals(2, entity.getGroupesEtabRelies().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertEquals("nomCCA", commercialEntity.getNomContactCommercial());
        Assertions.assertEquals("prenomCCA", commercialEntity.getPrenomContactCommercial());
        Assertions.assertEquals("mail@CCA.com", commercialEntity.getMailContactCommercial());
        Assertions.assertEquals("nomEditeur", commercialEntity.getEditeurEntity().getNomEditeur());
    }

    @Test
    @DisplayName("test nouvelEditeur 2CC 2CT")
    public void testMapperEditeurCreeDtoVersEditeurEntity2() {
        EditeurCreeDto editeurCreeDto = new EditeurCreeDto();
        editeurCreeDto.setNomEditeur("nomEditeur");
        editeurCreeDto.setAdresseEditeur("adressePostale");
        editeurCreeDto.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeDto.setGroupesEtabRelies(groupesEtabRelies);

        //2 CC
        ContactCommercialEditeurDto cc1 = new ContactCommercialEditeurDto("nomCCA", "prenomCCA", "mail@CCA.com");
        ContactCommercialEditeurDto cc2 = new ContactCommercialEditeurDto("nomCCB", "prenomCCB", "mail@CCB.com");

        Set<ContactCommercialEditeurDto> set = new HashSet<>();
        set.add(cc1);
        set.add(cc2);
        editeurCreeDto.setListeContactCommercialEditeurDto(set);

        //2 CT

        ContactTechniqueEditeurDto ct1 = new ContactTechniqueEditeurDto("nomCTA", "prenomCTA", "mail@CTA.com");
        ContactTechniqueEditeurDto ct2 = new ContactTechniqueEditeurDto("nomCTB", "prenomCTB", "mail@CTB.com");

        Set<ContactTechniqueEditeurDto> set2 = new HashSet<>();
        set2.add(ct1);
        set2.add(ct2);
        editeurCreeDto.setListeContactTechniqueEditeurDto(set2);

        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeurCreeDto);


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
        Assertions.assertEquals("nomEditeur", commercialEntity.getEditeurEntity().getNomEditeur());

        Assertions.assertEquals(2, entity.getContactTechniqueEditeurEntities().size());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertTrue(isValidNomT(techniqueEntity.getNomContactTechnique()));
        Assertions.assertTrue(isValidPrenomT(techniqueEntity.getPrenomContactTechnique()));
        Assertions.assertTrue(isValidMailT(techniqueEntity.getMailContactTechnique()));
        Assertions.assertEquals("nomEditeur", techniqueEntity.getEditeurEntity().getNomEditeur());

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
    @DisplayName("test supp Event Editeur")
    public void testMapperEditeurSupprimeEvent() {
        EditeurSupprimeDto editeurSupprimeDto = new EditeurSupprimeDto();
        editeurSupprimeDto.setId("21");

        EditeurSupprimeEvent editeurSupprimeEvent = new EditeurSupprimeEvent(this, Long.valueOf("21"));
        EditeurEntity entity = utilsMapper.map(editeurSupprimeEvent, EditeurEntity.class);

        Assertions.assertEquals("aaaaaaaaa", entity.getNomEditeur());
        Assertions.assertEquals("eeeeee", entity.getAdresseEditeur());
        Assertions.assertEquals("123", entity.getIdentifiantEditeur());
        Assertions.assertEquals(1, entity.getGroupesEtabRelies().size());
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

   /* @Test
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
    }*/
}
