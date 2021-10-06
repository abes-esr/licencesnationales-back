package fr.abes.licencesnationales.web.converter.etablissement;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.web.converter.ip.IpWebDtoConverter;
import fr.abes.licencesnationales.web.dto.etablissement.*;
import fr.abes.licencesnationales.web.dto.etablissement.creation.ContactCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeSansCaptchaWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.fusion.EtablissementFusionneWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.ContactModifieWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieAdminWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieUserWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.scission.EtablissementDiviseWebDto;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, EtablissementWebDtoConverter.class, ObjectMapper.class, IpWebDtoConverter.class})
public class EtablissementWebDtoConverterTest {
    @Autowired
    private UtilsMapper utilsMapper;


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
        etablissement.setNom("testNom");
        etablissement.setSiren("123456789");
        etablissement.setTypeEtablissement("testType");

        ContactModifieWebDto contact = new ContactModifieWebDto();
        contact.setNom("testNomContact");
        contact.setPrenom("testPrenomContact");
        contact.setAdresse("testAdresseContact");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        etablissement.setContact(contact);

        EtablissementModifieEventEntity event = utilsMapper.map(etablissement, EtablissementModifieEventEntity.class);

        Assertions.assertEquals("testNom", event.getNomEtab());
        Assertions.assertEquals("123456789", event.getSiren());
        Assertions.assertEquals("testType", event.getTypeEtablissement());
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

        etablissement.setNom("testNom");
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
        contact.setNom("testNomContact");
        contact.setPrenom("testPrenomContact");
        contact.setAdresse("testAdresseContact");
        contact.setCodePostal("testCP");
        contact.setVille("testVille");
        contact.setTelephone("0000000000");
        contact.setMail("test@test.com");
        etablissement.setContact(contact);

        EtablissementModifieEventEntity event = utilsMapper.map(etablissement, EtablissementModifieEventEntity.class);

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

        etablissement.getContact().setCodePostal("00000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'codePostal' du contact est obligatoire");

        etablissement.getContact().setVille("villeTest");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementModifieEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'ville' du contact est obligatoire");

    }

    @Test
    @DisplayName("Test success converter EtablissementFusionneWebDto / EtablissementFusionneEvent")
    public void testSuccessConverterEtabFusionneWebDto() {
        EtablissementFusionneWebDto dto = new EtablissementFusionneWebDto();
        dto.setSirenFusionnes(Lists.newArrayList("123456789", "987654321"));
        EtablissementCreeSansCaptchaWebDto dtoNouvelEtab = new EtablissementCreeSansCaptchaWebDto();
        dtoNouvelEtab.setNom("testNom");
        dtoNouvelEtab.setTypeEtablissement("testType");
        dtoNouvelEtab.setSiren("654987321");
        ContactCreeWebDto dtoContact = new ContactCreeWebDto();
        dtoContact.setNom("testNomContact");
        dtoContact.setPrenom("testPrenomContact");
        dtoContact.setMail("test@test.com");
        dtoContact.setAdresse("testAdresseContact");
        dtoContact.setCodePostal("testCP");
        dtoContact.setVille("testVille");
        dtoContact.setBoitePostale("BPContact");
        dtoContact.setTelephone("0000000000");
        dtoContact.setCedex("cedexContact");
        dtoNouvelEtab.setContact(dtoContact);
        dto.setNouveauEtab(dtoNouvelEtab);

        EtablissementFusionneEventEntity event = utilsMapper.map(dto, EtablissementFusionneEventEntity.class);

        Assertions.assertEquals("testNom", event.getNomEtab());
        Assertions.assertEquals("654987321", event.getSiren());
        Assertions.assertEquals("testType", event.getTypeEtablissement());
        Assertions.assertEquals("testNomContact", event.getNomContact());
        Assertions.assertEquals("testPrenomContact", event.getPrenomContact());
        Assertions.assertEquals("testAdresseContact", event.getAdresseContact());
        Assertions.assertEquals("testCP", event.getCodePostalContact());
        Assertions.assertEquals("testVille", event.getVilleContact());
        Assertions.assertEquals("0000000000", event.getTelephoneContact());
        Assertions.assertEquals("test@test.com", event.getMailContact());
        Assertions.assertEquals(2, event.getSirenAnciensEtablissements().size());
        Assertions.assertEquals("123456789", event.getSirenAnciensEtablissements().stream().findFirst().get());
        Assertions.assertEquals("987654321", event.getSirenAnciensEtablissements().stream().findFirst().get());
    }

    @Test
    @DisplayName("Test converter EtablissementFusionneWebDto / EtablissementFusionneEvent avec champs manquants")
    public void testMissingFieldsConverterEtabFusionneWebDto() {
        EtablissementFusionneWebDto etablissement = new EtablissementFusionneWebDto();
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("La fusion doit porter sur au moins 2 établissements");
        etablissement.setSirenFusionnes(Lists.newArrayList("123456789", "987654321"));

        EtablissementCreeSansCaptchaWebDto nouvelEtab = new EtablissementCreeSansCaptchaWebDto();
        etablissement.setNouveauEtab(nouvelEtab);

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'siren' est obligatoire");
        etablissement.getNouveauEtab().setSiren("654321987");

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' est obligatoire");

        etablissement.getNouveauEtab().setNom("nomEtab");

        ContactCreeWebDto contact = new ContactCreeWebDto();
        etablissement.getNouveauEtab().setContact(contact);

        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'nom' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setNom("testNom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'prenom' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setPrenom("testPrenom");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'telephone' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setTelephone("0000000000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'mail' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setMail("test@test.com");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'motDePasse' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setMotDePasse("testPassword");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'adresse' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setCodePostal("00000");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'codePostal' du contact est obligatoire");

        etablissement.getNouveauEtab().getContact().setVille("villeTest");
        Assertions.assertThrows(MappingException.class, () -> utilsMapper.map(etablissement, EtablissementFusionneEventEntity.class)).getErrorMessages().stream().findFirst().get().equals("Le champs 'ville' du contact est obligatoire");

    }

    @Test
    @DisplayName("test conversion EtablissementDiviseWebDto / EtablissementDiviseEventEntity")
    void testSuccessConverterEtabDiviseWebDto() {
        ContactCreeWebDto contact = new ContactCreeWebDto();
        contact.setNom("testScission");
        contact.setPrenom("testScission");
        contact.setAdresse("testScission");
        contact.setBoitePostale("34000");
        contact.setCodePostal("34444");
        contact.setVille("Motopp");
        contact.setTelephone("0000000000");
        contact.setMail("test@abes.fr");
        contact.setMotDePasse("!EtabTest3");

        EtablissementDiviseWebDto dto = new EtablissementDiviseWebDto();
        dto.setSirenScinde("000000000");
        EtablissementCreeSansCaptchaWebDto etab1 = new EtablissementCreeSansCaptchaWebDto();
        etab1.setNom("testScission 2");
        etab1.setTypeEtablissement("CHR-CHU");
        etab1.setSiren("000000001");
        etab1.setContact(contact);

        EtablissementCreeSansCaptchaWebDto etab2 = new EtablissementCreeSansCaptchaWebDto();
        etab2.setNom("testScission 3");
        etab2.setTypeEtablissement("CHR-CHU");
        etab2.setSiren("000000003");
        etab2.setContact(contact);

        dto.ajouterNouvelEtab(etab1);
        dto.ajouterNouvelEtab(etab2);

        EtablissementDiviseEventEntity event = utilsMapper.map(dto, EtablissementDiviseEventEntity.class);

        Assertions.assertEquals("000000000", event.getAncienSiren());
        Assertions.assertEquals("testScission 2", event.getEtablissementDivises().stream().findFirst().get().getNom());
        Assertions.assertEquals("000000001", event.getEtablissementDivises().stream().findFirst().get().getSiren());
    }

    @Test
    @DisplayName("test conversion EtablissementEntity / EtablissementUserWebDto")
    void testEtabUserWebDto() throws IpException {
        ContactEntity contact = new ContactEntity(1, "nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail@mail.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);
        StatutEntity ipStatut = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", (StatutIpEntity) ipStatut);
        IpEntity ip2 = new IpV6(2, "1111:1111:1111:1111:1111:1111:1111:1111", "test2", (StatutIpEntity) ipStatut);

        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);
        EtablissementUserWebDto dto = utilsMapper.map(etab, EtablissementUserWebDto.class);
        Assertions.assertEquals("nom2", dto.getContact().getNom());
        Assertions.assertEquals("prenom2", dto.getContact().getPrenom());
        Assertions.assertEquals("adresse2", dto.getContact().getAdresse());
        Assertions.assertEquals("BP2", dto.getContact().getBoitePostale());
        Assertions.assertEquals("11111", dto.getContact().getCodePostal());
        Assertions.assertEquals("ville2", dto.getContact().getVille());
        Assertions.assertEquals("cedex2", dto.getContact().getCedex());
        Assertions.assertEquals("1111111111", dto.getContact().getTelephone());
        Assertions.assertEquals("mail@mail.com", dto.getContact().getMail());
        Assertions.assertEquals("etab", dto.getContact().getRole());
        Assertions.assertEquals("1.1.1.1", dto.getIps().get(0).getIp());
        Assertions.assertEquals("test", dto.getIps().get(0).getCommentaires());
        Assertions.assertEquals("IPV4", dto.getIps().get(0).getTypeIp());
        Assertions.assertEquals("ip", dto.getIps().get(0).getTypeAcces());
        Assertions.assertEquals(1, dto.getIps().get(0).getId());
        Assertions.assertEquals("1111:1111:1111:1111:1111:1111:1111:1111", dto.getIps().get(1).getIp());
        Assertions.assertEquals("test2", dto.getIps().get(1).getCommentaires());
        Assertions.assertEquals(2, dto.getIps().get(1).getId());
        Assertions.assertEquals("IPV6", dto.getIps().get(1).getTypeIp());
        Assertions.assertEquals("ip", dto.getIps().get(1).getTypeAcces());

    }

    @Test
    @DisplayName("test conversion EtablissementEntity / EtablissementAdminWebDto")
    void testEtabAdminWebDto() {
        ContactEntity contact = new ContactEntity(1, "nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail@mail.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);

        EtablissementAdminWebDto dto = utilsMapper.map(etab, EtablissementAdminWebDto.class);

        Assertions.assertEquals(1, dto.getId().intValue());
        Assertions.assertEquals("123456789", dto.getSiren());
        Assertions.assertEquals("nomEtab", dto.getName());
        Assertions.assertEquals("123456", dto.getIdAbes());
        Assertions.assertEquals("validé", dto.getTypeEtablissement());
        Assertions.assertEquals("nom2", dto.getContact().getNom());
        Assertions.assertEquals("prenom2", dto.getContact().getPrenom());
        Assertions.assertEquals("adresse2", dto.getContact().getAdresse());
        Assertions.assertEquals("BP2", dto.getContact().getBoitePostale());
        Assertions.assertEquals("11111", dto.getContact().getCodePostal());
        Assertions.assertEquals("ville2", dto.getContact().getVille());
        Assertions.assertEquals("cedex2", dto.getContact().getCedex());
        Assertions.assertEquals("1111111111", dto.getContact().getTelephone());
        Assertions.assertEquals("mail@mail.com", dto.getContact().getMail());
        Assertions.assertEquals("etab", dto.getContact().getRole());
    }
}
