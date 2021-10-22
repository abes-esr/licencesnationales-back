package fr.abes.licencesnationales.web.converter.editeur;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurCreeEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurEventEntity;
import fr.abes.licencesnationales.core.entities.editeur.event.EditeurModifieEventEntity;
import fr.abes.licencesnationales.web.dto.editeur.*;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;

@Component
public class EditeurWebDtoConverter {
    @Autowired
    private UtilsMapper utilsMapper;

    @Bean
    public void converterEditeurCreeWebDto() {
        Converter<EditeurCreeWebDto, EditeurCreeEventEntity> myConverter = new Converter<EditeurCreeWebDto, EditeurCreeEventEntity>() {
            @SneakyThrows
            public EditeurCreeEventEntity convert(MappingContext<EditeurCreeWebDto, EditeurCreeEventEntity> context) {
                try {
                    EditeurCreeWebDto source = context.getSource();

                    EditeurCreeEventEntity editeurCreeEvent = new EditeurCreeEventEntity(this);
                    setEditeur(editeurCreeEvent, source.getNom(), source.getIdentifiantBis(), source.getAdresse(), source.getContactsCommerciaux(), source.getContactsTechniques());

                    return editeurCreeEvent;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }


    @Bean
    public void converterEditeurModifieWebDto() {

        Converter<EditeurModifieWebDto, EditeurModifieEventEntity> myConverter = new Converter<EditeurModifieWebDto, EditeurModifieEventEntity>() {

            public EditeurModifieEventEntity convert(MappingContext<EditeurModifieWebDto, EditeurModifieEventEntity> context) {
                try {
                    EditeurModifieWebDto source = context.getSource();

                    if (source.getId() == null) {
                        throw new IllegalArgumentException("Le champ 'id' est obligatoire");
                    }
                    EditeurModifieEventEntity editeurModifieEvent = new EditeurModifieEventEntity(this, source.getId());

                    setEditeur(editeurModifieEvent, source.getNom(), source.getIdentifiantBis(), source.getAdresse(), source.getContactsCommerciaux(), source.getContactsTechniques());
                    return editeurModifieEvent;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    private void setEditeur(EditeurEventEntity eventEntity, String nom, String identifiantBis, String adresse, Set<ContactEditeurWebDto> contactsCommerciaux, Set<ContactEditeurWebDto> contactsTechniques) {
        if (nom == null) {
            throw new IllegalArgumentException("Le champ 'nom' de l'éditeur est obligatoire");
        }
        eventEntity.setNom(nom);

        eventEntity.setIdentifiant(identifiantBis);

        if (adresse == null) {
            throw new IllegalArgumentException("Le champ 'adresse' de l'éditeur est obligatoire");
        }
        eventEntity.setAdresse(adresse);

        if ((contactsCommerciaux == null && contactsTechniques == null)
                && (contactsCommerciaux != null && contactsTechniques == null && contactsCommerciaux.size() == 0)
                && (contactsTechniques != null && contactsCommerciaux == null && contactsTechniques.size() == 0)) {
            throw new IllegalArgumentException("Au moins un 'contact commercial' ou un 'contact technique' est obligatoire");
        }
        try {
            for (ContactEditeurWebDto cc : contactsCommerciaux) {
                eventEntity.addContactCommercial(new ContactCommercialEditeurEntity(cc.getNomContact(), cc.getPrenomContact(), cc.getMailContact()));
            }
            for (ContactEditeurWebDto ct : contactsTechniques) {
                eventEntity.addContactTechnique(new ContactTechniqueEditeurEntity(ct.getNomContact(), ct.getPrenomContact(), ct.getMailContact()));
            }
        } catch (JsonProcessingException ex) {
            throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
        }
    }

    @Bean
    public void converterEditeurEntityWebDtoList() {

        Converter<EditeurEntity, EditeurWebDtoList> myConverter = new Converter<EditeurEntity, EditeurWebDtoList>() {

            public EditeurWebDtoList convert(MappingContext<EditeurEntity, EditeurWebDtoList> context) {
                EditeurEntity source = context.getSource();

                EditeurWebDtoList dto = new EditeurWebDtoList();
                dto.setNom(source.getNom());
                dto.setId(source.getId());
                dto.setDateCreation(source.getDateCreation());
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurEntityWebDto() {
        Converter<EditeurEntity, EditeurWebDto> myConverter = new Converter<EditeurEntity, EditeurWebDto>() {

            public EditeurWebDto convert(MappingContext<EditeurEntity, EditeurWebDto> context) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                EditeurEntity source = context.getSource();

                EditeurWebDto dto = new EditeurWebDto();
                dto.setId(source.getId());
                dto.setNom(source.getNom());
                dto.setIdentifiant(source.getIdentifiant());
                source.getTypeEtablissements().forEach(t -> dto.ajouterTypeEtablissement(t.getLibelle()));
                dto.setAdresse(source.getAdresse());
                dto.setDateCreation(dateFormat.format(source.getDateCreation()));
                source.getContactsCommerciaux().forEach(c -> dto.ajouterContactCommercial(utilsMapper.map(c, ContactEditeurWebDto.class)));
                source.getContactsTechniques().forEach(c -> dto.ajouterContactTechnique(utilsMapper.map(c, ContactEditeurWebDto.class)));
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterContactCommercialEditeurEntityWebDto() {
        Converter<ContactCommercialEditeurEntity, ContactEditeurWebDto> myConverter = new Converter<ContactCommercialEditeurEntity, ContactEditeurWebDto>() {

            public ContactEditeurWebDto convert(MappingContext<ContactCommercialEditeurEntity, ContactEditeurWebDto> context) {
                ContactEditeurEntity source = context.getSource();
                return getContactEditeurWebDto(source);
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterContactTechniqueEditeurEntityWebDto() {
        Converter<ContactTechniqueEditeurEntity, ContactEditeurWebDto> myConverter = new Converter<ContactTechniqueEditeurEntity, ContactEditeurWebDto>() {

            public ContactEditeurWebDto convert(MappingContext<ContactTechniqueEditeurEntity, ContactEditeurWebDto> context) {
                ContactEditeurEntity source = context.getSource();
                return getContactEditeurWebDto(source);
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    private ContactEditeurWebDto getContactEditeurWebDto(ContactEditeurEntity source) {
        ContactEditeurWebDto dto = new ContactEditeurWebDto();
        dto.setId(source.getId());
        dto.setNomContact(source.getNom());
        dto.setPrenomContact(source.getPrenom());
        dto.setMailContact(source.getMail());
        return dto;
    }
}
