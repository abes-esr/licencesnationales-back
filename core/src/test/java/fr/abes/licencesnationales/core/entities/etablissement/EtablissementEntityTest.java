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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @DisplayName("test validation creation contact sans ville ")
    void testValidateCreatEtabSansVille() {
        ContactEntity contact = new ContactEntity();
        contact.setPrenom("Samuel");
        contact.setNom("Quetin");
        contact.setAdresse("13 Rue du testàMontpellier");
        contact.setMail("sqn@abes.fr");
        contact.setCodePostal("34090");
        contact.setRole("etab");
        contact.setTelephone("0123456789");
        contact.setVille("");
        contact.setMotDePasse("aepojfovridehfgvgva65e6f54aze651daze321f45z6a5e54a24sdf321q3f46a45e");

        Set<ConstraintViolation<ContactEntity>> violations = validator.validate(contact);
        Optional<ConstraintViolation<ContactEntity>> violation = violations.stream().findFirst();

        Assertions.assertTrue(violation.isPresent());
        Assertions.assertEquals("ville", violation.get().getPropertyPath().toString());
        Assertions.assertEquals("La ville fournie n'est pas valide", violation.get().getMessage());
    }

    @Test
    @DisplayName("test validation creation contact sans prenom ")
    void testValidateCreatEtabSansPrenom() {
        ContactEntity contact = new ContactEntity();
        contact.setPrenom("");
        contact.setNom("");
        contact.setAdresse("");
        contact.setMail("");
        contact.setCodePostal("");
        contact.setRole("");
        contact.setTelephone("");
        contact.setVille("");
        contact.setMotDePasse("");

        Set<ConstraintViolation<ContactEntity>> violations = validator.validate(contact);
        List<ConstraintViolation<ContactEntity>> list = new ArrayList<>(violations);
        Assertions.assertEquals(7,list.size());
        Assertions.assertEquals("telephone",list.get(0).getPropertyPath().toString());
        Assertions.assertEquals("Veuillez entrer 10 chiffres sans espace",list.get(0).getMessage());
        Assertions.assertEquals("codePostal",list.get(1).getPropertyPath().toString());
        Assertions.assertEquals("Le code postal fourni n'est pas valide",list.get(1).getMessage());
        Assertions.assertEquals("adresse",list.get(2).getPropertyPath().toString());
        Assertions.assertEquals("L'adresse postale fournie n'est pas valide",list.get(2).getMessage());
        Assertions.assertEquals("ville",list.get(3).getPropertyPath().toString());
        Assertions.assertEquals("La ville fournie n'est pas valide",list.get(3).getMessage());
        Assertions.assertEquals("mail",list.get(4).getPropertyPath().toString());
        Assertions.assertEquals("L'adresse mail fournie n'est pas valide",list.get(4).getMessage());
        Assertions.assertEquals("nom",list.get(5).getPropertyPath().toString());
        Assertions.assertEquals("Le nom fourni n'est pas valide",list.get(5).getMessage());
        Assertions.assertEquals("prenom",list.get(6).getPropertyPath().toString());
        Assertions.assertEquals("Le prénom fourni n'est pas valide",list.get(6).getMessage());

    }
}
