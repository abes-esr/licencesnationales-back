package fr.abes.licencesnationales.web.converter.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
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
        Converter<EditeurCreeWebDto, EditeurCreeEvent> myConverter = new Converter<EditeurCreeWebDto, EditeurCreeEvent>() {

            public EditeurCreeEvent convert(MappingContext<EditeurCreeWebDto, EditeurCreeEvent> context) {
                EditeurCreeWebDto source = context.getSource();

                EditeurCreeEvent editeurCreeEvent = new EditeurCreeEvent(this);
                editeurCreeEvent.setNomEditeur(source.getNomEditeur());
                editeurCreeEvent.setIdentifiantEditeur(source.getIdentifiantEditeur());
                editeurCreeEvent.setAdresseEditeur(source.getAdresseEditeur());
                editeurCreeEvent.setGroupesEtabRelies(source.getGroupesEtabRelies());

                editeurCreeEvent.setListeContactCommercialEditeur(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                editeurCreeEvent.setListeContactTechniqueEditeur(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));

                return editeurCreeEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurModifieWebDto() {
        Converter<EditeurModifieWebDto, EditeurModifieEvent> myConverter = new Converter<EditeurModifieWebDto, EditeurModifieEvent>() {

            public EditeurModifieEvent convert(MappingContext<EditeurModifieWebDto, EditeurModifieEvent> context) {
                EditeurModifieWebDto source = context.getSource();

                EditeurModifieEvent editeurModifieEvent = new EditeurModifieEvent(this);
                editeurModifieEvent.setId(source.getIdEditeur());
                editeurModifieEvent.setNomEditeur(source.getNomEditeur());
                editeurModifieEvent.setIdentifiantEditeur(source.getIdentifiantEditeur());
                editeurModifieEvent.setAdresseEditeur(source.getAdresseEditeur());
                editeurModifieEvent.setGroupesEtabRelies(source.getGroupesEtabRelies());

                editeurModifieEvent.setListeContactCommercialEditeur(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                editeurModifieEvent.setListeContactTechniqueEditeur(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));

                return editeurModifieEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
