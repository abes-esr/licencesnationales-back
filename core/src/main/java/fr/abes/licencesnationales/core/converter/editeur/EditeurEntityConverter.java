package fr.abes.licencesnationales.core.converter.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurFusionneeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
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
        Converter<EditeurCreeEventEntity, EditeurEntity> myConverter = new Converter<EditeurCreeEventEntity, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurCreeEventEntity, EditeurEntity> context) {
                EditeurCreeEventEntity source = context.getSource();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(source.getNomEditeur());
                entity.setIdentifiantEditeur(source.getIdentifiantEditeur());
                entity.setAdresseEditeur(source.getAdresseEditeur());
                entity.setGroupesEtabRelies(source.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));
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
        Converter<EditeurModifieEventEntity, EditeurEntity> myConverter = new Converter<EditeurModifieEventEntity, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurModifieEventEntity, EditeurEntity> context) {
                EditeurModifieEventEntity source = context.getSource();

                EditeurEntity entity = new EditeurEntity();
                entity.setIdEditeur(source.getId());
                entity.setNomEditeur(source.getNomEditeur());
                entity.setIdentifiantEditeur(source.getIdentifiantEditeur());
                entity.setAdresseEditeur(source.getAdresseEditeur());
                entity.setGroupesEtabRelies(source.getGroupesEtabRelies());
                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));
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
        Converter<EditeurFusionneeEventEntity, EditeurEntity> myConverter = new Converter<EditeurFusionneeEventEntity, EditeurEntity>() {

            public EditeurEntity convert(MappingContext<EditeurFusionneeEventEntity, EditeurEntity> context) {
                EditeurFusionneeEventEntity source = context.getSource();

                EditeurEntity entity = new EditeurEntity();
                entity.setNomEditeur(source.getNomEditeur());
                entity.setIdentifiantEditeur(source.getIdentifiantEditeur());
                entity.setAdresseEditeur(source.getAdresseEditeur());
                entity.setGroupesEtabRelies(source.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    @Bean
    public void converterContactEditeurEntityContactEditeurDto() {
        Converter<ContactEditeurEntity, ContactEditeurDto> myConverter = new Converter<ContactEditeurEntity, ContactEditeurDto>() {

            public ContactEditeurDto convert(MappingContext<ContactEditeurEntity, ContactEditeurDto> context) {
                ContactEditeurEntity source = context.getSource();

                ContactEditeurDto dto = new ContactEditeurDto();
                dto.setNomContact(source.getNomContact());
                dto.setPrenomContact(source.getPrenomContact());
                dto.setMailContact(source.getMailContact());
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    @Bean
    public void converterContactEditeurDtoContactTechniqueEditeurEntity() {
        Converter<ContactEditeurDto, ContactTechniqueEditeurEntity> myConverter = new Converter<ContactEditeurDto, ContactTechniqueEditeurEntity>() {

            public ContactTechniqueEditeurEntity convert(MappingContext<ContactEditeurDto, ContactTechniqueEditeurEntity> context) {
                ContactEditeurDto source = context.getSource();

                ContactTechniqueEditeurEntity entity = new ContactTechniqueEditeurEntity();
                entity.setNomContact(source.getNomContact());
                entity.setPrenomContact(source.getPrenomContact());
                entity.setMailContact(source.getMailContact());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    @Bean
    public void converterContactEditeurDtoContactCommercialEditeurEntity() {
        Converter<ContactEditeurDto, ContactCommercialEditeurEntity> myConverter = new Converter<ContactEditeurDto, ContactCommercialEditeurEntity>() {

            public ContactCommercialEditeurEntity convert(MappingContext<ContactEditeurDto, ContactCommercialEditeurEntity> context) {
                ContactEditeurDto source = context.getSource();

                ContactCommercialEditeurEntity entity = new ContactCommercialEditeurEntity();
                entity.setNomContact(source.getNomContact());
                entity.setPrenomContact(source.getPrenomContact());
                entity.setMailContact(source.getMailContact());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

}
