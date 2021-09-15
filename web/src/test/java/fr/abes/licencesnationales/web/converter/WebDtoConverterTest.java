package fr.abes.licencesnationales.web.converter;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.services.GenererIdAbes;
import fr.abes.licencesnationales.web.dto.ContactWebDto;
import fr.abes.licencesnationales.web.dto.editeur.ContactCommercialEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.ContactTechniqueEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementCreeWebDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WebDtoConverterTest {
    @Autowired
    private UtilsMapper utilsMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GenererIdAbes genererIdAbes;

    @Test
    @DisplayName("test converter EditeurCreeWebDto / EditeurCreeEvent")
    public void testConverterEditeurCreeWebDto() {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        EditeurCreeWebDto editeur = new EditeurCreeWebDto();
        editeur.setNomEditeur("testNom");
        editeur.setAdresseEditeur("testAdresse");
        editeur.setIdentifiantEditeur("testId");

        List<String> listEtabRelie = new ArrayList<>();
        listEtabRelie.add("etabRelie1");
        listEtabRelie.add("etabRelie2");
        editeur.setGroupesEtabRelies(listEtabRelie);

        ContactCommercialEditeurWebDto contactCommercial = new ContactCommercialEditeurWebDto("testNomComm", "testPrenomComm", "test@test.comm");
        ContactTechniqueEditeurWebDto contactTechnique = new ContactTechniqueEditeurWebDto("testNomTech", "testPrenomTech", "test@test.tech");

        Set<ContactCommercialEditeurWebDto> setContactComm = new HashSet<>();
        setContactComm.add(contactCommercial);
        editeur.setListeContactCommercialEditeur(setContactComm);

        Set<ContactTechniqueEditeurWebDto> setContactTech = new HashSet<>();
        setContactTech.add(contactTechnique);
        editeur.setListeContactTechniqueEditeur(setContactTech);

        EditeurCreeEvent editeurCreeEvent = utilsMapper.map(editeur, EditeurCreeEvent.class);

        Assertions.assertEquals("testNom", editeurCreeEvent.getNomEditeur());
        Assertions.assertEquals("testAdresse", editeurCreeEvent.getAdresseEditeur());
        Assertions.assertEquals("testId", editeurCreeEvent.getIdentifiantEditeur());
        Assertions.assertEquals(format.format(new Date()), format.format(editeurCreeEvent.getDateCreation()));
        Assertions.assertEquals("etabRelie1", editeurCreeEvent.getGroupesEtabRelies().get(0));
        Assertions.assertEquals("etabRelie2", editeurCreeEvent.getGroupesEtabRelies().get(1));
        Assertions.assertEquals("testNomComm", editeurCreeEvent.getListeContactCommercialEditeur().stream().findFirst().get().getNomContact());
        Assertions.assertEquals("testPrenomComm", editeurCreeEvent.getListeContactCommercialEditeur().stream().findFirst().get().getPrenomContact());
        Assertions.assertEquals("test@test.comm", editeurCreeEvent.getListeContactCommercialEditeur().stream().findFirst().get().getMailContact());
        Assertions.assertEquals("testNomTech", editeurCreeEvent.getListeContactTechniqueEditeur().stream().findFirst().get().getNomContact());
        Assertions.assertEquals("testPrenomTech", editeurCreeEvent.getListeContactTechniqueEditeur().stream().findFirst().get().getPrenomContact());
        Assertions.assertEquals("test@test.tech", editeurCreeEvent.getListeContactTechniqueEditeur().stream().findFirst().get().getMailContact());

    }

    @Test
    @DisplayName("test converter EditeurModifieWebDto / EditeurModifieEvent")
    public void testConverterEditeurModifieWebDto() {
        EditeurModifieWebDto editeur = new EditeurModifieWebDto();
        editeur.setIdEditeur(1);
        editeur.setNomEditeur("testNom");
        editeur.setAdresseEditeur("testAdresse");
        editeur.setIdentifiantEditeur("testId");

        List<String> listEtabRelie = new ArrayList<>();
        listEtabRelie.add("etabRelie1");
        listEtabRelie.add("etabRelie2");
        editeur.setGroupesEtabRelies(listEtabRelie);

        ContactCommercialEditeurWebDto contactCommercial = new ContactCommercialEditeurWebDto("testNomComm", "testPrenomComm", "test@test.comm");
        ContactTechniqueEditeurWebDto contactTechnique = new ContactTechniqueEditeurWebDto("testNomTech", "testPrenomTech", "test@test.tech");

        Set<ContactCommercialEditeurWebDto> setContactComm = new HashSet<>();
        setContactComm.add(contactCommercial);
        editeur.setListeContactCommercialEditeur(setContactComm);

        Set<ContactTechniqueEditeurWebDto> setContactTech = new HashSet<>();
        setContactTech.add(contactTechnique);
        editeur.setListeContactTechniqueEditeur(setContactTech);

        EditeurModifieEvent editeurModifieEvent = utilsMapper.map(editeur, EditeurModifieEvent.class);

        Assertions.assertEquals(Long.toString(1L), editeurModifieEvent.getId().toString());
        Assertions.assertEquals("testNom", editeurModifieEvent.getNomEditeur());
        Assertions.assertEquals("testAdresse", editeurModifieEvent.getAdresseEditeur());
        Assertions.assertEquals("testId", editeurModifieEvent.getIdentifiantEditeur());
        Assertions.assertEquals("etabRelie1", editeurModifieEvent.getGroupesEtabRelies().get(0));
        Assertions.assertEquals("etabRelie2", editeurModifieEvent.getGroupesEtabRelies().get(1));
        Assertions.assertEquals("testNomComm", editeurModifieEvent.getListeContactCommercialEditeur().stream().findFirst().get().getNomContact());
        Assertions.assertEquals("testPrenomComm", editeurModifieEvent.getListeContactCommercialEditeur().stream().findFirst().get().getPrenomContact());
        Assertions.assertEquals("test@test.comm", editeurModifieEvent.getListeContactCommercialEditeur().stream().findFirst().get().getMailContact());
        Assertions.assertEquals("testNomTech", editeurModifieEvent.getListeContactTechniqueEditeur().stream().findFirst().get().getNomContact());
        Assertions.assertEquals("testPrenomTech", editeurModifieEvent.getListeContactTechniqueEditeur().stream().findFirst().get().getPrenomContact());
        Assertions.assertEquals("test@test.tech", editeurModifieEvent.getListeContactTechniqueEditeur().stream().findFirst().get().getMailContact());

    }

    @Test
    @DisplayName("Test converter EtablissementCreeWebDto / EtablissementCreeEvent")
    public void testConverterEtabCreeWebDto() {
        EtablissementCreeWebDto etablissement = new EtablissementCreeWebDto();
        etablissement.setName("testNom");
        etablissement.setSiren("123456789");
        etablissement.setTypeEtablissement("testType");
        etablissement.setMotDePasse("testPassword");
        etablissement.setIdAbes("123456789");
        ContactWebDto contact = new ContactWebDto();
        contact.setNom("testNomContact");
        contact.setPrenom("testPrenomContact");
        contact.setAdresse("testAdresseContact");
        contact.setBoitePostale("testBP");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        etablissement.setContact(contact);
        etablissement.setRoleContact("etab");
        etablissement.setRecaptcha("testRecaptcha");

        EtablissementCreeEvent event = utilsMapper.map(etablissement, EtablissementCreeEvent.class);

        Assertions.assertEquals("testNom", event.getNom());
        Assertions.assertEquals("123456789", event.getSiren());
        Assertions.assertEquals("testType", event.getTypeEtablissement());
        Assertions.assertTrue(passwordEncoder.matches("testPassword", event.getMotDePasse()));
        Assertions.assertEquals(genererIdAbes.genererIdAbes("123456789"), event.getIdAbes());
        Assertions.assertEquals("testNomContact", event.getNomContact());
        Assertions.assertEquals("testPrenomContact", event.getPrenomContact());
        Assertions.assertEquals("testAdresseContact", event.getAdresseContact());
        Assertions.assertEquals("testBP", event.getBoitePostaleContact());
        Assertions.assertEquals("testCP", event.getCodePostalContact());
        Assertions.assertEquals("testVille", event.getVilleContact());
        Assertions.assertEquals("0000000000", event.getTelephoneContact());
        Assertions.assertEquals("test@test.com", event.getMailContact());
        Assertions.assertEquals("etab", event.getRoleContact());
        Assertions.assertEquals("testRecaptcha", event.getRecaptcha());


    }
}
