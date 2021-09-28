package fr.abes.licencesnationales.web.converter.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EditeurWebDtoConverter {
    @Autowired
    private UtilsMapper utilsMapper;

    @Bean
    public void converterEditeurCreeWebDto() {
        Converter<EditeurCreeWebDto, EditeurCreeEventEntity> myConverter = new Converter<EditeurCreeWebDto, EditeurCreeEventEntity>() {
            @SneakyThrows
            public EditeurCreeEventEntity convert(MappingContext<EditeurCreeWebDto, EditeurCreeEventEntity> context) {
                EditeurCreeWebDto source = context.getSource();

                EditeurCreeEventEntity editeurCreeEvent = new EditeurCreeEventEntity(this);
                editeurCreeEvent.setNomEditeur(source.getNomEditeur());
                editeurCreeEvent.setIdentifiantEditeur(source.getIdentifiantEditeur());
                editeurCreeEvent.setAdresseEditeur(source.getAdresseEditeur());
                editeurCreeEvent.setTypesEtabs(source.getGroupesEtabRelies());

                editeurCreeEvent.setListeContactCommercialEditeur(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactEditeurDto.class));
                editeurCreeEvent.setListeContactTechniqueEditeur(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactEditeurDto.class));


                return editeurCreeEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurModifieWebDto() {

        Converter<EditeurModifieWebDto, EditeurModifieEventEntity> myConverter = new Converter<EditeurModifieWebDto, EditeurModifieEventEntity>() {

            public EditeurModifieEventEntity convert(MappingContext<EditeurModifieWebDto, EditeurModifieEventEntity> context) {
                EditeurModifieWebDto source = context.getSource();

                EditeurModifieEventEntity editeurModifieEvent = new EditeurModifieEventEntity(this, source.getIdEditeur(), source.getGroupesEtabRelies());

                editeurModifieEvent.setNomEditeur(source.getNomEditeur());
                editeurModifieEvent.setIdentifiantEditeur(source.getIdentifiantEditeur());
                editeurModifieEvent.setAdresseEditeur(source.getAdresseEditeur());
                editeurModifieEvent.setTypesEtabs(source.getGroupesEtabRelies());

                editeurModifieEvent.setListeContactCommercialEditeur(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactEditeurDto.class));
                editeurModifieEvent.setListeContactTechniqueEditeur(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactEditeurDto.class));


                return editeurModifieEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
