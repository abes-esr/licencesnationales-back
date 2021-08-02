package fr.abes.licencesnationales;

import fr.abes.licencesnationales.core.entities.ContactEntity;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;

import java.util.Date;

public class MockUserUtil {
    private MockUserUtil() {
    }
    /**
     *
     */
    public static EtablissementEntity getMockUser() {
        EtablissementEntity user = new EtablissementEntity();
        user.setId(1L);
        user.setName("test");
        user.setIdAbes("123456789");
        user.setDateCreation(new Date());
        user.setSiren("123456789");
        user.setValide(true);
        user.setTypeEtablissement("test");
        ContactEntity contact = new ContactEntity(1L, "nomTest", "prenomTest", "mailTest", "passwordTest", "TelTest", "adresseTest", "BPTest", "CPTest", "CedexTest", "villeTest", "etab");
        user.setContact(contact);
        return user;
    }
}
