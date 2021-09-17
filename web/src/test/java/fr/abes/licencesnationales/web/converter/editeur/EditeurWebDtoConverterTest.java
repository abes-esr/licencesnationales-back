package fr.abes.licencesnationales.web.converter.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.web.dto.editeur.ContactCommercialEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.ContactTechniqueEditeurWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
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
@SpringBootTest
public class EditeurWebDtoConverterTest {
    @Autowired
    private UtilsMapper utilsMapper;

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

        EditeurCreeEventEntity editeurCreeEvent = utilsMapper.map(editeur, EditeurCreeEventEntity.class);

        Assertions.assertEquals("testNom", editeurCreeEvent.getNomEditeur());
        Assertions.assertEquals("testAdresse", editeurCreeEvent.getAdresseEditeur());
        Assertions.assertEquals("testId", editeurCreeEvent.getIdentifiantEditeur());
        Assertions.assertEquals(format.format(new Date()), format.format(editeurCreeEvent.getDateCreationEvent()));
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

        EditeurModifieEventEntity editeurModifieEvent = utilsMapper.map(editeur, EditeurModifieEventEntity.class);

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
}
