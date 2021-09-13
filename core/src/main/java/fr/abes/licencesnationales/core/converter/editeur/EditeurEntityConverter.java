package fr.abes.licencesnationales.core.converter.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Cette classe recense tous les convertisseurs d'objet événement sur les éditeurs vers les objets d'entité
 */
@Component
public class EditeurEntityConverter {

    @Autowired
    private UtilsMapper utilsMapper;

    /**
     * Bean de conversion d'un événement de création d'éditeur
     */
    @Bean
    public void converterEditeurCreeEvent() {
        Converter<EditeurCreeEvent, EditeurEntity> myConverter = new Converter<EditeurCreeEvent, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurCreeEvent, EditeurEntity> context) {
                EditeurCreeEvent source = context.getSource();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(source.getNomEditeur());
                entity.setIdentifiantEditeur(source.getIdentifiantEditeur());
                entity.setAdresseEditeur(source.getAdresseEditeur());
                entity.setGroupesEtabRelies(source.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(source.getListeContactCommercialEditeur());
                entity.setContactTechniqueEditeurEntities(source.getListeContactTechniqueEditeur());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    /**
     * Bean de conversion d'un événement de modification d'éditeur
     */
    @Bean
    public void converterEditeurModifieEvent() {
        Converter<EditeurModifieEvent, EditeurEntity> myConverter = new Converter<EditeurModifieEvent, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurModifieEvent, EditeurEntity> context) {
                EditeurModifieEvent source = context.getSource();

                EditeurEntity entity = new EditeurEntity();
                entity.setIdEditeur(source.getId());
                entity.setNomEditeur(source.getNomEditeur());
                entity.setIdentifiantEditeur(source.getIdentifiantEditeur());
                entity.setAdresseEditeur(source.getAdresseEditeur());
                entity.setGroupesEtabRelies(source.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(source.getListeContactCommercialEditeur());
                entity.setContactTechniqueEditeurEntities(source.getListeContactTechniqueEditeur());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    /**
     * Bean de conversion d'un événement de fusion d'éditeur
     */
    @Bean
    public void converterEditeurFusionneEvent() {
        Converter<EditeurFusionneEvent, EditeurEntity> myConverter = new Converter<EditeurFusionneEvent, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurFusionneEvent, EditeurEntity> context) {
                EditeurFusionneEvent source = context.getSource();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(source.getNomEditeur());
                entity.setIdentifiantEditeur(source.getIdentifiantEditeur());
                entity.setAdresseEditeur(source.getAdresseEditeur());
                entity.setGroupesEtabRelies(source.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(source.getListeContactCommercialEditeur());
                entity.setContactTechniqueEditeurEntities(source.getListeContactTechniqueEditeur());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }
}
