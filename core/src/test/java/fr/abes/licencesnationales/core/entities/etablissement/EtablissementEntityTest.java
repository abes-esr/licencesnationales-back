package fr.abes.licencesnationales.core.entities.etablissement;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@ExtendWith(SpringExtension.class)
public class EtablissementEntityTest {

    private EtablissementEntity etab;
    private StatutIpEntity statutIpNouvelle;
    private StatutIpEntity statutIpAttestation;
    private StatutIpEntity statutIpValidee;

    private Validator validator;

    @BeforeEach
    void init() {
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        etab = new EtablissementEntity("test", "123456789", new TypeEtablissementEntity(1, "testType"), "123456", contact);

        statutIpNouvelle = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "Nouvelle IP");
        statutIpAttestation = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        statutIpValidee = new StatutIpEntity(Constant.STATUT_IP_VALIDEE, "IP Validée");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    @DisplayName("test méthode de mise à jour du statut en fonction des statuts d'Ips : cas liste IP vide")
    void testSetStatutFromEmptyIpList() {
        Assertions.assertEquals(Constant.STATUT_ETAB_SANSIP, etab.getStatut());
    }

    @Test
    @DisplayName("test méthode de mise à jour du statut en fonction des statuts d'Ips : cas 1 IP de chaque statut d'IP possible")
    void testSetStatutFromIpList1() throws IpException {
        IpEntity ip1 = new IpV4("1.1.1.1", "test", statutIpNouvelle);
        IpEntity ip2 = new IpV4("2.2.2.2", "test2", statutIpAttestation);
        IpEntity ip3 = new IpV4("3.3.3.3", "test3", statutIpValidee);

        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);
        etab.ajouterIp(ip3);

        Assertions.assertEquals(Constant.STATUT_ETAB_EXAMINERIP, etab.getStatut());

    }

    @Test
    @DisplayName("test méthode de mise à jour du statut en fonction des statuts d'Ips : cas 2 statut sur les 3 possibles")
    void testSetStatutFromIpList2() throws IpException {
        IpEntity ip2 = new IpV4("2.2.2.2", "test2", statutIpAttestation);
        IpEntity ip3 = new IpV4("3.3.3.3", "test3", statutIpValidee);

        etab.ajouterIp(ip2);
        etab.ajouterIp(ip3);

        Assertions.assertEquals(Constant.STATUT_ETAB_ATTENTEATTESTATION, etab.getStatut());
    }

    @Test
    @DisplayName("test méthode de mise à jour du statut en fonction des statuts d'Ips : cas beaucoup d'IPs dans le désordre")
    void testSetStatutFromIpList3() throws IpException {
        IpEntity ip1 = new IpV4("1.1.1.1", "test1", statutIpAttestation);
        IpEntity ip2 = new IpV4("2.2.2.2", "test2", statutIpAttestation);
        IpEntity ip3 = new IpV4("3.3.3.3", "test3", statutIpValidee);
        IpEntity ip4 = new IpV4("4.4.4.4", "test2", statutIpAttestation);
        IpEntity ip5 = new IpV4("5.5.5.5", "test3", statutIpValidee);
        IpEntity ip6 = new IpV4("6.6.6.6", "test4", statutIpNouvelle);

        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);
        etab.ajouterIp(ip3);
        etab.ajouterIp(ip4);
        etab.ajouterIp(ip5);
        etab.ajouterIp(ip6);

        ;
        Assertions.assertEquals(Constant.STATUT_ETAB_EXAMINERIP, etab.getStatut());
    }

    @Test
    @DisplayName("test méthode de mise à jour du statut en fonction des statuts d'Ips : cas beaucoup d'IPs dans le désordre 2")
    void testSetStatutFromIpList4() throws IpException {
        IpEntity ip1 = new IpV4("1.1.1.1", "test1", statutIpAttestation);
        IpEntity ip2 = new IpV4("2.2.2.2", "test2", statutIpAttestation);
        IpEntity ip3 = new IpV4("3.3.3.3", "test3", statutIpValidee);
        IpEntity ip4 = new IpV4("4.4.4.4", "test2", statutIpAttestation);
        IpEntity ip5 = new IpV4("5.5.5.5", "test3", statutIpValidee);

        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);
        etab.ajouterIp(ip3);
        etab.ajouterIp(ip4);
        etab.ajouterIp(ip5);

        Assertions.assertEquals(Constant.STATUT_ETAB_ATTENTEATTESTATION, etab.getStatut());
    }

    @Test
    @DisplayName("test validation creation contact Blank ")
    void testValidateCreatContactBlank() {
        ContactEntity contact = new ContactEntity("", "", "", "", "", "", "", "","","");

        Set<ConstraintViolation<ContactEntity>> violationsContact = validator.validate(contact);
        Assertions.assertEquals(7,violationsContact.size());
        Assertions.assertEquals("Veuillez entrer 10 chiffres sans espace", violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("telephone")).findFirst().get().getMessage());
        Assertions.assertEquals("Le code postal fourni n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("codePostal")).findFirst().get().getMessage());
        Assertions.assertEquals("L'adresse postale fournie n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("adresse")).findFirst().get().getMessage());
        Assertions.assertEquals("La ville fournie n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("ville")).findFirst().get().getMessage());
        Assertions.assertEquals("L'adresse mail fournie n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("mail")).findFirst().get().getMessage());
        Assertions.assertEquals("Le nom fourni n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("nom")).findFirst().get().getMessage());
        Assertions.assertEquals("Le prénom fourni n'est pas valide", violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("prenom")).findFirst().get().getMessage());
    }

    @Test
    @DisplayName("test validation creation contact null ")
    void testValidateCreatContactNull() {
        ContactEntity contact = new ContactEntity(null, null, null, null, null, null, null, null,null,null);

        Set<ConstraintViolation<ContactEntity>> violationsContact = validator.validate(contact);
        Assertions.assertEquals(7,violationsContact.size());
        Assertions.assertEquals("ne doit pas être nul", violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("telephone")).findFirst().get().getMessage());
        Assertions.assertEquals("ne doit pas être nul",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("codePostal")).findFirst().get().getMessage());
        Assertions.assertEquals("ne doit pas être nul",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("adresse")).findFirst().get().getMessage());
        Assertions.assertEquals("La ville fournie n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("ville")).findFirst().get().getMessage());
        Assertions.assertEquals("ne doit pas être nul",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("mail")).findFirst().get().getMessage());
        Assertions.assertEquals("Le nom fourni n'est pas valide",violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("nom")).findFirst().get().getMessage());
        Assertions.assertEquals("Le prénom fourni n'est pas valide", violationsContact.stream().filter(v -> v.getPropertyPath().toString().equals("prenom")).findFirst().get().getMessage());
    }

    @Test
    @DisplayName("test validation creation etablissement Blank ")
    void testValidateCreatEtablissemenBlank() {
        EtablissementEntity etab = new EtablissementEntity("","",new TypeEtablissementEntity(1,""),"",new ContactEntity("","","","","","","",""));

        Set<ConstraintViolation<EtablissementEntity>> violationsEtab = validator.validate(etab);
        Assertions.assertEquals(2,violationsEtab.size());
        Assertions.assertEquals("Le nom d'établissement fourni n'est pas valide", violationsEtab.stream().filter(v -> v.getPropertyPath().toString().equals("nom")).findFirst().get().getMessage());
        Assertions.assertEquals(Constant.SIREN_DOIT_CONTENIR_9_CHIFFRES, violationsEtab.stream().filter(v -> v.getPropertyPath().toString().equals("siren")).findFirst().get().getMessage());
    }

    @Test
    @DisplayName("test validation creation etablissement Null ")
    void testValidateCreatEtablissemenNull() {
        EtablissementEntity etab = new EtablissementEntity(null,null,new TypeEtablissementEntity(1,""),"",new ContactEntity("","","","","","","",""));

        Set<ConstraintViolation<EtablissementEntity>> violationsEtab = validator.validate(etab);
        Assertions.assertEquals(2,violationsEtab.size());
        Assertions.assertEquals("Le nom d'établissement fourni n'est pas valide", violationsEtab.stream().filter(v -> v.getPropertyPath().toString().equals("nom")).findFirst().get().getMessage());
        Assertions.assertEquals("ne doit pas être nul", violationsEtab.stream().filter(v -> v.getPropertyPath().toString().equals("siren")).findFirst().get().getMessage());

    }
}
