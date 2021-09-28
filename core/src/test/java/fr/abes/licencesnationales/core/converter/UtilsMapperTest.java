package fr.abes.licencesnationales.core.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.editeur.EditeurEntityConverter;
import fr.abes.licencesnationales.core.converter.ip.Ipv4RangeConverter;
import fr.abes.licencesnationales.core.converter.ip.Ipv6RangeConverter;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, Ipv4RangeConverter.class, Ipv6RangeConverter.class, EditeurEntityConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;


    @MockBean
    private TypeEtablissementRepository repository;

    @Test
    @DisplayName("test nouvelEditeur 1CC 0CT")
    void testMapperEditeurCreeEventVersEditeurEntity1() throws JsonProcessingException {
        EditeurCreeEventEntity editeurCreeEvent = new EditeurCreeEventEntity(this);

        editeurCreeEvent.setNomEditeur("nomEditeur");
        editeurCreeEvent.setAdresseEditeur("adressePostale");
        editeurCreeEvent.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeEvent.setGroupesEtabRelies(groupesEtabRelies);

        ContactCommercialEditeurEntity contactComm = new ContactCommercialEditeurEntity("nomCCA", "prenomCCA", "mail@CCA.com");
        Set<ContactCommercialEditeurEntity> setComm = new HashSet<>();
        setComm.add(contactComm);
        editeurCreeEvent.setListeContactCommercialEditeur(utilsMapper.mapSet(setComm, ContactEditeurDto.class));

        ContactTechniqueEditeurEntity contactTech = new ContactTechniqueEditeurEntity("testNomTech", "testPrenomTech", "test@test.tech");
        Set<ContactTechniqueEditeurEntity> setTech = new HashSet<>();
        setTech.add(contactTech);
        editeurCreeEvent.setListeContactTechniqueEditeur(utilsMapper.mapSet(setTech, ContactEditeurDto.class));

        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("nomEditeur", entity.getNomEditeur());
        Assertions.assertEquals("adressePostale", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertEquals("nomCCA", commercialEntity.getNomContact());
        Assertions.assertEquals("prenomCCA", commercialEntity.getPrenomContact());
        Assertions.assertEquals("mail@CCA.com", commercialEntity.getMailContact());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertEquals("testNomTech", techniqueEntity.getNomContact());
        Assertions.assertEquals("testPrenomTech", techniqueEntity.getPrenomContact());
        Assertions.assertEquals("test@test.tech", techniqueEntity.getMailContact());
    }

    @Test
    @DisplayName("test nouvelEditeur 2CC 2CT")
    void testMapperEditeurCreeEventVersEditeurEntity2() {
        EditeurCreeEventEntity editeurCreeEvent = new EditeurCreeEventEntity(this);

        editeurCreeEvent.setNomEditeur("nomEditeur");
        editeurCreeEvent.setAdresseEditeur("adressePostale");
        editeurCreeEvent.setIdentifiantEditeur("123456");
        List<String> groupesEtabRelies = new ArrayList<>();
        groupesEtabRelies.add("EPIC/EPST");
        groupesEtabRelies.add("Ecoles d'ingénieurs");
        editeurCreeEvent.setGroupesEtabRelies(groupesEtabRelies);

        ContactCommercialEditeurEntity cc1 = new ContactCommercialEditeurEntity("nomCCA", "prenomCCA", "mail@CCA.com");
        ContactCommercialEditeurEntity cc2 = new ContactCommercialEditeurEntity("nomCCB", "prenomCCB", "mail@CCB.com");

        Set<ContactCommercialEditeurEntity> set = new HashSet<>();
        set.add(cc1);
        set.add(cc2);
        editeurCreeEvent.setListeContactCommercialEditeur(utilsMapper.mapSet(set, ContactEditeurDto.class));

        ContactTechniqueEditeurEntity ct1 = new ContactTechniqueEditeurEntity("nomCTA", "prenomCTA", "mail@CTA.com");
        ContactTechniqueEditeurEntity ct2 = new ContactTechniqueEditeurEntity("nomCTB", "prenomCTB", "mail@CTB.com");

        Set<ContactTechniqueEditeurEntity> set2 = new HashSet<>();
        set2.add(ct1);
        set2.add(ct2);
        editeurCreeEvent.setListeContactTechniqueEditeur(utilsMapper.mapSet(set2, ContactEditeurDto.class));


        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assertions.assertEquals("nomEditeur", entity.getNomEditeur());
        Assertions.assertEquals("adressePostale", entity.getAdresseEditeur());
        Assertions.assertEquals("123456", entity.getIdentifiantEditeur());

        Assertions.assertEquals(2, entity.getContactCommercialEditeurEntities().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assertions.assertTrue(isValidNomC(commercialEntity.getNomContact()));
        Assertions.assertTrue(isValidPrenomC(commercialEntity.getPrenomContact()));
        Assertions.assertTrue(isValidMailC(commercialEntity.getMailContact()));

        Assertions.assertEquals(2, entity.getContactTechniqueEditeurEntities().size());
        ContactTechniqueEditeurEntity techniqueEntity = entity.getContactTechniqueEditeurEntities().iterator().next();
        Assertions.assertTrue(isValidNomT(techniqueEntity.getNomContact()));
        Assertions.assertTrue(isValidPrenomT(techniqueEntity.getPrenomContact()));
        Assertions.assertTrue(isValidMailT(techniqueEntity.getMailContact()));

    }
    private Boolean isValidNomC(String s) {
        return (s.equals("nomCCA") || s.equals("nomCCB"));
    }
    private Boolean isValidNomT(String s) {
        return (s.equals("nomCTA") || s.equals("nomCTB"));
    }
    private Boolean isValidPrenomC(String s) {
        return (s.equals("prenomCCA") || s.equals("prenomCCB"));
    }
    private Boolean isValidPrenomT(String s) {
        return (s.equals("prenomCTA") || s.equals("prenomCTB"));
    }
    private Boolean isValidMailC(String s) {
        return ("mail@CCA.com".equals(s) || "mail@CCB.com".equals(s));
    }
    private Boolean isValidMailT(String s) {
        return ("mail@CTA.com".equals(s) || "mail@CTB.com".equals(s));
    }


}
