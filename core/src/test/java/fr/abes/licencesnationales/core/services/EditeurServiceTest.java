package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.repository.contactediteur.ContactEditeurRepository;
import fr.abes.licencesnationales.core.repository.editeur.EditeurRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EditeurService.class})
public class EditeurServiceTest {
    @Autowired
    private EditeurService service;

    @MockBean
    private DateEnvoiEditeurRepository date;

    @MockBean
    private EditeurRepository editeurRepository;

    @MockBean
    private ContactEditeurRepository contactEditeurRepository;


    @DisplayName("test doublon email")
    @Test
    void testDoublonEmail() throws MailDoublonException {
        Set<ContactTechniqueEditeurEntity> ctSet = new HashSet<>();
        ContactTechniqueEditeurEntity ct1 = new ContactTechniqueEditeurEntity("nom1", "prenom1", "mail1@mail.com");
        ContactTechniqueEditeurEntity ct2 = new ContactTechniqueEditeurEntity("nom2", "prenom2", "mail2@mail.com");
        ctSet.add(ct1);
        ctSet.add(ct2);

        Set<ContactCommercialEditeurEntity> ccSet = new HashSet<>();
        ContactCommercialEditeurEntity cc1 = new ContactCommercialEditeurEntity("nom3", "prenom3", "mail3@mail.com");
        ContactCommercialEditeurEntity cc2 = new ContactCommercialEditeurEntity("nom4", "prenom4", "mail4@mail.com");
        ccSet.add(cc1);
        ccSet.add(cc2);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);
        EditeurEntity editeur = new EditeurEntity(1, "nom", "12345", "adresse", setType);
        editeur.setContactsCommerciaux(ccSet);
        editeur.setContactsTechniques(ctSet);

        Mockito.when(contactEditeurRepository.findByMailContains("mail1@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactEditeurRepository.findByMailContains("mail2@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactEditeurRepository.findByMailContains("mail3@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactEditeurRepository.findByMailContains("mail4@mail.com")).thenReturn(Optional.empty());

        service.checkDoublonMail(editeur);

        Mockito.when(contactEditeurRepository.findByMailContains("mail1@mail.com")).thenReturn(Optional.of(new ContactTechniqueEditeurEntity("nomTest", "prenomTest", "mail1@mail.com")));
        Assertions.assertThrows(MailDoublonException.class, () -> service.checkDoublonMail(editeur), "L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");

        Mockito.when(contactEditeurRepository.findByMailContains("mail1@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactEditeurRepository.findByMailContains("mail3@mail.com")).thenReturn(Optional.of(new ContactCommercialEditeurEntity("nomTest", "prenomTest", "mail3@mail.com")));
        Assertions.assertThrows(MailDoublonException.class, () -> service.checkDoublonMail(editeur), "L'adresse mail renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
    }

    @DisplayName("test search editeur")
    @Test
    void testSearchEditeur() {
        Set<ContactTechniqueEditeurEntity> ctSet = new HashSet<>();
        ContactTechniqueEditeurEntity ct1 = new ContactTechniqueEditeurEntity("nom1", "prenom1", "mail1@mail.com");
        ContactTechniqueEditeurEntity ct2 = new ContactTechniqueEditeurEntity("nom2", "prenom2", "mail2@mail.com");
        ctSet.add(ct1);
        ctSet.add(ct2);

        Set<ContactCommercialEditeurEntity> ccSet = new HashSet<>();
        ContactCommercialEditeurEntity cc1 = new ContactCommercialEditeurEntity("nom3", "prenom3", "mail3@mail.com");
        ContactCommercialEditeurEntity cc2 = new ContactCommercialEditeurEntity("nom4", "prenom4", "mail4@mail.com");
        ccSet.add(cc1);
        ccSet.add(cc2);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);
        EditeurEntity editeur1 = new EditeurEntity(1, "nom", "12345", "adresse", setType);
        editeur1.setContactsCommerciaux(ccSet);
        editeur1.setContactsTechniques(ctSet);

        EditeurEntity editeur2 = new EditeurEntity(2, "nom2", "654897842", "adresse", setType);
        editeur2.ajouterContact(cc2);
        editeur2.ajouterContact(ct2);

        List<EditeurEntity> listEditeur = new ArrayList<>();
        listEditeur.add(editeur1);
        listEditeur.add(editeur2);

        Mockito.when(editeurRepository.findAll()).thenReturn(listEditeur);

        List<String> criteres = new ArrayList<>();
        criteres.add("654897842");
        List<EditeurEntity> resultat = service.search(criteres);
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals(2, resultat.get(0).getId());

        criteres = new ArrayList<>();
        criteres.add("nom3");
        resultat = service.search(criteres);
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals(1, resultat.get(0).getId());

        criteres.add("12345");
        resultat = service.search(criteres);
        Assertions.assertEquals(1, resultat.size());
        Assertions.assertEquals(1, resultat.get(0).getId());

        criteres.add("klnklsdmfjsdlk");
        resultat = service.search(criteres);
        Assertions.assertEquals(0, resultat.size());

    }
}
