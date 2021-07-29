package fr.abes.licencesnationales.converter;

import fr.abes.licencesnationales.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.dto.editeur.EditeurFusionneDto;
import fr.abes.licencesnationales.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.entities.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.entities.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.entities.EditeurEntity;
import fr.abes.licencesnationales.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.event.editeur.EditeurModifieEvent;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EditeurConverter {
    @Autowired
    private UtilsMapper utilsMapper;

    @Bean
    public void converterEditeurCreeEvent() {

        Converter<EditeurCreeEvent, EditeurEntity> myConverter = new Converter<EditeurCreeEvent, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurCreeEvent, EditeurEntity> context) {
                EditeurCreeEvent source = context.getSource();
                EditeurCreeDto dto = source.getEditeur();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(dto.getNomEditeur());
                entity.setIdentifiantEditeur(dto.getIdentifiantEditeur());
                entity.setAdresseEditeur(dto.getAdresseEditeur());
                entity.setGroupesEtabRelies(dto.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(dto.getListeContactCommercialEditeurEventDto(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(dto.getListeContactTechniqueEditeurEventDto(), ContactTechniqueEditeurEntity.class));
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurModifieEvent() {

        Converter<EditeurModifieEvent, EditeurEntity> myConverter = new Converter<EditeurModifieEvent, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurModifieEvent, EditeurEntity> context) {
                EditeurModifieEvent source = context.getSource();
                EditeurModifieDto dto = source.getEditeur();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(dto.getNomEditeur());
                entity.setIdentifiantEditeur(dto.getIdentifiantEditeur());
                entity.setAdresseEditeur(dto.getAdresseEditeur());
                entity.setGroupesEtabRelies(dto.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(dto.getListeContactCommercialEditeurEventDto(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(dto.getListeContactTechniqueEditeurEventDto(), ContactTechniqueEditeurEntity.class));
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurFusionneEvent() {
        Converter<EditeurFusionneEvent, EditeurEntity> myConverter = new Converter<EditeurFusionneEvent, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurFusionneEvent, EditeurEntity> context) {
                EditeurFusionneEvent source = context.getSource();
                EditeurFusionneDto dto = source.getEditeur();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(dto.getNomEditeur());
                entity.setIdentifiantEditeur(dto.getIdentifiantEditeur());
                entity.setAdresseEditeur(dto.getAdresseEditeur());
                entity.setGroupesEtabRelies(dto.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(dto.getListeContactCommercialEditeurEventDto(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(dto.getListeContactTechniqueEditeurEventDto(), ContactTechniqueEditeurEntity.class));
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

}
