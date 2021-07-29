package fr.abes.licencesnationales.converter;

import fr.abes.licencesnationales.dto.editeur.ContactCommercialEditeurEventDto;
import fr.abes.licencesnationales.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.entities.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.entities.EditeurEntity;
import fr.abes.licencesnationales.event.editeur.EditeurCreeEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UtilsMapper.class, EditeurConverter.class})
public class UtilsMapperTest {
    @Autowired
    private UtilsMapper utilsMapper;

    @Test
    @DisplayName("test création Event Editeur")
    public void testMapperEditeurCreeEvent() {
        EditeurCreeDto editeurCreeDto = new EditeurCreeDto();
        editeurCreeDto.setNomEditeur("testNom");
        editeurCreeDto.setAdresseEditeur("testAdresse");
        editeurCreeDto.setIdentifiantEditeur("123456");
        editeurCreeDto.setGroupesEtabRelies(Arrays.asList("1", "2"));

        EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this, editeurCreeDto);

        ContactCommercialEditeurEventDto commercial = new ContactCommercialEditeurEventDto("testNom", "testPrenom", "test@test.com");
        Set<ContactCommercialEditeurEventDto> set = new HashSet<>();
        set.add(commercial);
        editeurCreeDto.setListeContactCommercialEditeurEventDto(set);

        EditeurEntity entity = utilsMapper.map(editeurCreeEvent, EditeurEntity.class);

        Assert.assertEquals("testNom", entity.getNomEditeur());
        Assert.assertEquals("testAdresse", entity.getAdresseEditeur());
        Assert.assertEquals("123456", entity.getIdentifiantEditeur());
        Assert.assertEquals(2, entity.getGroupesEtabRelies().size());
        ContactCommercialEditeurEntity commercialEntity = entity.getContactCommercialEditeurEntities().iterator().next();
        Assert.assertEquals("testNom", commercialEntity.getNomContactCommercial());
    }
}
