package fr.abes.licencesnationales.web.converter.etablissement;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, EtablissementWebDtoConverter.class, ObjectMapper.class})
public class EtablissementWebDtoConverterTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @InjectMocks
    private EtablissementWebDtoConverter etablissementWebDtoConverter;

    @MockBean
    private TypeEtablissementRepository repository;

    @Test
    @DisplayName("Test success converter EtablissementCreeWebDto / EtablissementCreeEvent")
    public void testSuccessConverterEtabCreeWebDto() {
        EtablissementCreeWebDto etablissement = new EtablissementCreeWebDto();
        etablissement.setName("testNom");
        etablissement.setSiren("123456789");
        etablissement.setTypeEtablissement("testType");
        etablissement.setRecaptcha("testRecaptcha");

        ContactCreeWebDto contact = new ContactCreeWebDto();

        contact.setMotDePasse("testPassword");
        contact.setNom("testNomContact");
        contact.setPrenom("testPrenomContact");
        contact.setAdresse("testAdresseContact");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        etablissement.setContact(contact);

        EtablissementCreeEventEntity event = utilsMapper.map(etablissement, EtablissementCreeEventEntity.class);

        Assertions.assertEquals("testNom", event.getNomEtab());
        Assertions.assertEquals("123456789", event.getSiren());
        Assertions.assertEquals("testType", event.getTypeEtablissement());
        Assertions.assertEquals("testPassword", event.getMotDePasse());
        Assertions.assertEquals("testNomContact", event.getNomContact());
        Assertions.assertEquals("testPrenomContact", event.getPrenomContact());
        Assertions.assertEquals("testAdresseContact", event.getAdresseContact());
        Assertions.assertEquals("testCP", event.getCodePostalContact());
        Assertions.assertEquals("testVille", event.getVilleContact());
        Assertions.assertEquals("0000000000", event.getTelephoneContact());
        Assertions.assertEquals("test@test.com", event.getMailContact());
    }

    @Test
    @DisplayName("Test converter EtablissementCreeWebDto / EtablissementCreeEvent avec champs manquants")
    public void testMissingFieldsConverterEtabCreeWebDto() {
        EtablissementCreeWebDto etablissement = new EtablissementCreeWebDto();

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' est obligatoire");

        etablissement.setName("testNom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'siren' est obligatoire");

        etablissement.setSiren("123456789");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'typeEtablissement' est obligatoire");

        etablissement.setTypeEtablissement("testType");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'contact' est obligatoire");

        ContactCreeWebDto contact = new ContactCreeWebDto();
        etablissement.setContact(contact);
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' du contact est obligatoire");

        etablissement.getContact().setNom("testNom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'prenom' du contact est obligatoire");

        etablissement.getContact().setPrenom("testPrenom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'telephone' du contact est obligatoire");

        etablissement.getContact().setTelephone("0000000000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'mail' du contact est obligatoire");

        etablissement.getContact().setMail("test@test.com");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'motDePasse' du contact est obligatoire");

        etablissement.getContact().setMotDePasse("testPassword");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'adresse' du contact est obligatoire");

        etablissement.getContact().setCodePostal("00000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'codePostal' du contact est obligatoire");

        etablissement.getContact().setVille("villeTest");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementCreeEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'ville' du contact est obligatoire");

    }

    @Test
    @DisplayName("Test success converter EtablissementModifieAdminWebDto / EtablissementModifieEvent")
    public void testSuccessConverterEtabModifieAdminWebDto() {
        EtablissementModifieAdminWebDto etablissement = new EtablissementModifieAdminWebDto();
        etablissement.setName("testNom");
        etablissement.setSiren("123456789");
        etablissement.setTypeEtablissement("testType");

        ContactModifieWebDto contact = new ContactModifieWebDto();
        contact.setMotDePasse("testPassword");
        contact.setNom("testNomContact");
        contact.setPrenom("testPrenomContact");
        contact.setAdresse("testAdresseContact");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        etablissement.setContact(contact);

        EtablissementModifieEventEntity event = utilsMapper.map(etablissement, EtablissementModifieEventEntity.class);

        Assertions.assertEquals(1, event.getId().intValue());
        Assertions.assertEquals("testNom", event.getNomEtab());
        Assertions.assertEquals("123456789", event.getSiren());
        Assertions.assertEquals("testType", event.getTypeEtablissement());
        Assertions.assertEquals("testPassword", event.getMotDePasse());
        Assertions.assertEquals("testNomContact", event.getNomContact());
        Assertions.assertEquals("testPrenomContact", event.getPrenomContact());
        Assertions.assertEquals("testAdresseContact", event.getAdresseContact());
        Assertions.assertEquals("testCP", event.getCodePostalContact());
        Assertions.assertEquals("testVille", event.getVilleContact());
        Assertions.assertEquals("0000000000", event.getTelephoneContact());
        Assertions.assertEquals("test@test.com", event.getMailContact());
    }

    @Test
    @DisplayName("Test converter EtablissementModifieAdminWebDto / EtablissementModifieEvent avec champs manquants")
    public void testMissingFieldsConverterEtabModifieAdminWebDto() {
        EtablissementModifieAdminWebDto etablissement = new EtablissementModifieAdminWebDto();

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' est obligatoire");

        etablissement.setName("testNom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'siren' est obligatoire");

        etablissement.setSiren("123456789");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'typeEtablissement' est obligatoire");

        etablissement.setTypeEtablissement("testType");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'contact' est obligatoire");

        ContactModifieWebDto contact = new ContactModifieWebDto();
        etablissement.setContact(contact);

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' du contact est obligatoire");

        etablissement.getContact().setNom("testNom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'prenom' du contact est obligatoire");

        etablissement.getContact().setPrenom("testPrenom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'telephone' du contact est obligatoire");

        etablissement.getContact().setTelephone("0000000000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'mail' du contact est obligatoire");

        etablissement.getContact().setMail("test@test.com");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'motDePasse' du contact est obligatoire");

        etablissement.getContact().setMotDePasse("testPassword");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'adresse' du contact est obligatoire");

        etablissement.getContact().setCodePostal("00000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'codePostal' du contact est obligatoire");

        etablissement.getContact().setVille("villeTest");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'ville' du contact est obligatoire");

    }

    @Test
    @DisplayName("Test success converter EtablissementModifieWebDto / EtablissementModifieEvent")
    public void testSuccessConverterEtabModifieWebDto() {
        EtablissementModifieUserWebDto etablissement = new EtablissementModifieUserWebDto();

        ContactModifieWebDto contact = new ContactModifieWebDto();
        contact.setMotDePasse("testPassword");
        contact.setNom("testNomContact");
        contact.setPrenom("testPrenomContact");
        contact.setAdresse("testAdresseContact");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        etablissement.setContact(contact);

        EtablissementModifieEventEntity event = utilsMapper.map(etablissement, EtablissementModifieEventEntity.class);

        Assertions.assertEquals(1, event.getId().intValue());
        Assertions.assertEquals("testPassword", event.getMotDePasse());
        Assertions.assertEquals("testNomContact", event.getNomContact());
        Assertions.assertEquals("testPrenomContact", event.getPrenomContact());
        Assertions.assertEquals("testAdresseContact", event.getAdresseContact());
        Assertions.assertEquals("testCP", event.getCodePostalContact());
        Assertions.assertEquals("testVille", event.getVilleContact());
        Assertions.assertEquals("0000000000", event.getTelephoneContact());
        Assertions.assertEquals("test@test.com", event.getMailContact());
    }

    @Test
    @DisplayName("Test converter EtablissementModifieWebDto / EtablissementModifieEvent avec champs manquants")
    public void testMissingFieldsConverterEtabModifieWebDto() {
        EtablissementModifieUserWebDto etablissement = new EtablissementModifieUserWebDto();

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' est obligatoire");

        ContactModifieWebDto contact = new ContactModifieWebDto();
        etablissement.setContact(contact);

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' du contact est obligatoire");

        etablissement.getContact().setNom("testNom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'prenom' du contact est obligatoire");

        etablissement.getContact().setPrenom("testPrenom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'telephone' du contact est obligatoire");

        etablissement.getContact().setTelephone("0000000000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'mail' du contact est obligatoire");

        etablissement.getContact().setMail("test@test.com");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'motDePasse' du contact est obligatoire");

        etablissement.getContact().setMotDePasse("testPassword");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'adresse' du contact est obligatoire");

        etablissement.getContact().setCodePostal("00000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'codePostal' du contact est obligatoire");

        etablissement.getContact().setVille("villeTest");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'ville' du contact est obligatoire");

    }
}
