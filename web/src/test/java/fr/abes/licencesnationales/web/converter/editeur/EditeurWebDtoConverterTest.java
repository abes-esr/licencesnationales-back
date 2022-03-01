package fr.abes.licencesnationales.web.converter.editeur;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.web.dto.editeur.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, ObjectMapper.class, EditeurWebDtoConverter.class})
public class EditeurWebDtoConverterTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @Test
    @DisplayName("test converter EditeurEntity / WebDtoList")
    void testConverterEntityWebDtoList() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);

        EditeurEntity entity = new EditeurEntity(1, "nom", "123456", "adresse", setType);

        EditeurWebDtoList dto = utilsMapper.map(entity, EditeurWebDtoList.class);

        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("nom", dto.getNom());

    }

    @Test
    @DisplayName("test converter EditeurEntity / Webdto")
    void testConverterEntityWebDto() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);

        EditeurEntity entity = new EditeurEntity(1, "nom", "123456", "adresse", setType);
        ContactEditeurEntity contactCommercial = new ContactCommercialEditeurEntity(1, "nomComm", "prenomComm", "mail@mail.com");
        ContactEditeurEntity contactTechnique = new ContactTechniqueEditeurEntity(2, "nomTech", "prenomTech", "mail@mail.tech");
        entity.ajouterContact(contactCommercial);
        entity.ajouterContact(contactTechnique);

        EditeurWebDto dto = utilsMapper.map(entity, EditeurWebDto.class);

        Assertions.assertEquals("nom", dto.getNom());
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("123456", dto.getIdentifiant());
        Assertions.assertEquals("adresse", dto.getAdresse());
        Assertions.assertEquals("Nouveau", dto.getTypesEtablissements().get(0));

        Assertions.assertEquals(1, dto.getContactsCommerciaux().get(0).getId());
        Assertions.assertEquals("nomComm", dto.getContactsCommerciaux().get(0).getNomContact());
        Assertions.assertEquals("prenomComm", dto.getContactsCommerciaux().get(0).getPrenomContact());
        Assertions.assertEquals("mail@mail.com", dto.getContactsCommerciaux().get(0).getMailContact());

        Assertions.assertEquals(2, dto.getContactsTechniques().get(0).getId());
        Assertions.assertEquals("nomTech", dto.getContactsTechniques().get(0).getNomContact());
        Assertions.assertEquals("prenomTech", dto.getContactsTechniques().get(0).getPrenomContact());
        Assertions.assertEquals("mail@mail.tech", dto.getContactsTechniques().get(0).getMailContact());
    }

    @Test
    @DisplayName("test converter EditeurCreeWebDto / EditeurCreeEvent")
    void testConverterEditeurCreeWebDto() {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        EditeurCreeWebDto editeur = new EditeurCreeWebDto();
        editeur.setNom("testNom");
        editeur.setAdresse("testAdresse");
        editeur.setIdentifiantBis("testId");

        ContactEditeurWebDto contactCommercial = new ContactEditeurWebDto("testNomComm", "testPrenomComm", "test@test.comm");
        ContactEditeurWebDto contactTechnique = new ContactEditeurWebDto("testNomTech", "testPrenomTech", "test@test.tech");

        Set<ContactEditeurWebDto> setContactComm = new HashSet<>();
        setContactComm.add(contactCommercial);
        editeur.setContactsCommerciaux(setContactComm);

        Set<ContactEditeurWebDto> setContactTech = new HashSet<>();
        setContactTech.add(contactTechnique);
        editeur.setContactsTechniques(setContactTech);

        EditeurCreeEventEntity editeurCreeEvent = utilsMapper.map(editeur, EditeurCreeEventEntity.class);

        Assertions.assertEquals("testNom", editeurCreeEvent.getNom());
        Assertions.assertEquals("testAdresse", editeurCreeEvent.getAdresse());
        Assertions.assertEquals("testId", editeurCreeEvent.getIdentifiant());
        Assertions.assertEquals(format.format(new Date()), format.format(editeurCreeEvent.getDateCreationEvent()));
        Assertions.assertEquals("testNomComm", editeurCreeEvent.getContactsCommerciaux().stream().findFirst().get().getNom());
        Assertions.assertEquals("testPrenomComm", editeurCreeEvent.getContactsCommerciaux().stream().findFirst().get().getPrenom());
        Assertions.assertEquals("test@test.comm", editeurCreeEvent.getContactsCommerciaux().stream().findFirst().get().getMail());
        Assertions.assertEquals("testNomTech", editeurCreeEvent.getContactsTechniques().stream().findFirst().get().getNom());
        Assertions.assertEquals("testPrenomTech", editeurCreeEvent.getContactsTechniques().stream().findFirst().get().getPrenom());
        Assertions.assertEquals("test@test.tech", editeurCreeEvent.getContactsTechniques().stream().findFirst().get().getMail());

    }

    @Test
    @DisplayName("test converter EditeurModifieWebDto / EditeurModifieEvent")
    public void testConverterEditeurModifieWebDto() {
        EditeurModifieWebDto editeur = new EditeurModifieWebDto();
        editeur.setId(1);
        editeur.setNom("testNom");
        editeur.setAdresse("testAdresse");
        editeur.setIdentifiantBis("testId");

        ContactEditeurWebDto contactCommercial = new ContactEditeurWebDto("testNomComm", "testPrenomComm", "test@test.comm");
        ContactEditeurWebDto contactTechnique = new ContactEditeurWebDto("testNomTech", "testPrenomTech", "test@test.tech");

        Set<ContactEditeurWebDto> setContactComm = new HashSet<>();
        setContactComm.add(contactCommercial);
        editeur.setContactsCommerciaux(setContactComm);

        Set<ContactEditeurWebDto> setContactTech = new HashSet<>();
        setContactTech.add(contactTechnique);
        editeur.setContactsTechniques(setContactTech);

        EditeurModifieEventEntity editeurModifieEvent = utilsMapper.map(editeur, EditeurModifieEventEntity.class);

        Assertions.assertEquals(Long.toString(1L), editeurModifieEvent.getId().toString());
        Assertions.assertEquals(1, editeurModifieEvent.getId());
        Assertions.assertEquals("testNom", editeurModifieEvent.getNom());
        Assertions.assertEquals("testAdresse", editeurModifieEvent.getAdresse());
        Assertions.assertEquals("testId", editeurModifieEvent.getIdentifiant());
        Assertions.assertEquals("testNomComm", editeurModifieEvent.getContactsCommerciaux().stream().findFirst().get().getNom());
        Assertions.assertEquals("testPrenomComm", editeurModifieEvent.getContactsCommerciaux().stream().findFirst().get().getPrenom());
        Assertions.assertEquals("test@test.comm", editeurModifieEvent.getContactsCommerciaux().stream().findFirst().get().getMail());
        Assertions.assertEquals("testNomTech", editeurModifieEvent.getContactsTechniques().stream().findFirst().get().getNom());
        Assertions.assertEquals("testPrenomTech", editeurModifieEvent.getContactsTechniques().stream().findFirst().get().getPrenom());
        Assertions.assertEquals("test@test.tech", editeurModifieEvent.getContactsTechniques().stream().findFirst().get().getMail());

    }

    @Test
    @DisplayName("test converter EditeurEntity / SearchWebdto")
    void testConverterEntitySearchWebDto() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);

        EditeurEntity entity = new EditeurEntity(1, "nom", "123456", "adresse", setType);
        ContactEditeurEntity contactCommercial = new ContactCommercialEditeurEntity(1, "nomComm", "prenomComm", "mail@mail.com");
        ContactEditeurEntity contactTechnique = new ContactTechniqueEditeurEntity(2, "nomTech", "prenomTech", "mail@mail.tech");
        entity.ajouterContact(contactCommercial);
        entity.ajouterContact(contactTechnique);

        EditeurSearchWebDto dto = utilsMapper.map(entity, EditeurSearchWebDto.class);

        Assertions.assertEquals("nom", dto.getNom());
        Assertions.assertEquals(1, dto.getId());
        Assertions.assertEquals("123456", dto.getIdEditeur());
        Assertions.assertEquals("adresse", dto.getAdresse());

        Assertions.assertEquals(1, dto.getContactsCommerciaux().get(0).getId());
        Assertions.assertEquals("nomComm", dto.getContactsCommerciaux().get(0).getNomContact());
        Assertions.assertEquals("prenomComm", dto.getContactsCommerciaux().get(0).getPrenomContact());
        Assertions.assertEquals("mail@mail.com", dto.getContactsCommerciaux().get(0).getMailContact());

        Assertions.assertEquals(2, dto.getContactsTechniques().get(0).getId());
        Assertions.assertEquals("nomTech", dto.getContactsTechniques().get(0).getNomContact());
        Assertions.assertEquals("prenomTech", dto.getContactsTechniques().get(0).getPrenomContact());
        Assertions.assertEquals("mail@mail.tech", dto.getContactsTechniques().get(0).getMailContact());
    }
}
