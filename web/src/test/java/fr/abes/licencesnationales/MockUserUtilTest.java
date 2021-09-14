package fr.abes.licencesnationales;

import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

public class MockUserUtilTest {
    private final PasswordEncoder passwordEncoder;

    public MockUserUtilTest(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    /**
     *
     */
    public EtablissementEntity getMockUser() {
        EtablissementEntity user = new EtablissementEntity();
        user.setId(1L);
        user.setName("test");
        user.setIdAbes("123456789");
        user.setDateCreation(new Date());
        user.setSiren("123456789");
        user.setStatut(new StatutEtablissementEntity());
        user.setTypeEtablissement("test");
        ContactEntity contact = new ContactEntity(1L, "nomTest", "prenomTest", "mailTest@test.com", passwordEncoder.encode("OldPass1Test&"), "TelTest", "adresseTest", "BPTest", "CPTest", "CedexTest", "villeTest", "etab");
        user.setContact(contact);
        return user;
    }

}
