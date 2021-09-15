package fr.abes.licencesnationales.web.converter;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementDiviseEvent;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.GenererIdAbes;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurModifieWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementCreeWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.EtablissementDiviseWebDto;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.internal.Errors;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class WebDtoConverter {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtilsMapper utilsMapper;

    @Autowired
    private GenererIdAbes genererIdAbes;

    @Autowired
    private TypeEtablissementRepository repository;

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

    @Bean
    public void converterEtablissementCreeWebDto() {
        Converter<EtablissementCreeWebDto, EtablissementCreeEvent> myConverter = new Converter<EtablissementCreeWebDto, EtablissementCreeEvent>() {

            public EtablissementCreeEvent convert(MappingContext<EtablissementCreeWebDto, EtablissementCreeEvent> context) {
                EtablissementCreeWebDto source = context.getSource();

                EtablissementCreeEvent etablissementCreeEvent = new EtablissementCreeEvent(this);
                etablissementCreeEvent.setNom(source.getName());
                etablissementCreeEvent.setSiren(source.getSiren());
                etablissementCreeEvent.setTypeEtablissement(source.getTypeEtablissement());
                etablissementCreeEvent.setIdAbes(genererIdAbes.genererIdAbes(source.getIdAbes()));
                etablissementCreeEvent.setNomContact(source.getContact().getNom());
                etablissementCreeEvent.setPrenomContact(source.getContact().getPrenom());
                etablissementCreeEvent.setAdresseContact(source.getContact().getAdresse());
                etablissementCreeEvent.setBoitePostaleContact(source.getContact().getBoitePostale());
                etablissementCreeEvent.setCodePostalContact(source.getContact().getCodePostal());
                etablissementCreeEvent.setVilleContact(source.getContact().getVille());
                etablissementCreeEvent.setCedexContact(source.getContact().getCedex());
                etablissementCreeEvent.setTelephoneContact(source.getContact().getTelephone());
                etablissementCreeEvent.setMailContact(source.getContact().getMail());
                etablissementCreeEvent.setMotDePasse(passwordEncoder.encode(source.getMotDePasse()));
                etablissementCreeEvent.setRoleContact("etab");
                etablissementCreeEvent.setRecaptcha(source.getRecaptcha());

                return etablissementCreeEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementDiviseWebDto() {
        Converter<EtablissementDiviseWebDto, EtablissementDiviseEvent> myConverter = new Converter<EtablissementDiviseWebDto, EtablissementDiviseEvent>() {
            @SneakyThrows
            public EtablissementDiviseEvent convert(MappingContext<EtablissementDiviseWebDto, EtablissementDiviseEvent> context) {
                EtablissementDiviseWebDto source = context.getSource();

                EtablissementDiviseEvent etablissementDiviseEvent = new EtablissementDiviseEvent(this);
                etablissementDiviseEvent.setAncienSiren(source.getAncienSiren());
                source.getEtablissementDtos().stream().forEach(e -> {
                    EtablissementEntity etablissement = new EtablissementEntity();
                    etablissement.setName(e.getName());
                    etablissement.setSiren(e.getSiren());
                    Optional<TypeEtablissementEntity> type = repository.findFirstByLibelle(e.getTypeEtablissement());
                    if (!type.isPresent()) {
                        throw new Errors().errorMapping(source, context.getDestinationType(), new UnknownTypeEtablissementException("Type d'établissement inconnu")).toMappingException();
                    }
                    etablissement.setTypeEtablissement(type.get());
                    etablissement.setIdAbes(e.getIdAbes());
                    ContactEntity contact = new ContactEntity();
                    contact.setNom(e.getContact().getNom());
                    contact.setPrenom(e.getContact().getPrenom());
                    contact.setAdresse(e.getContact().getAdresse());
                    contact.setBoitePostale(e.getContact().getBoitePostale());
                    contact.setCodePostal(e.getContact().getCodePostal());
                    contact.setVille(e.getContact().getVille());
                    contact.setCedex(e.getContact().getCedex());
                    contact.setTelephone(e.getContact().getTelephone());
                    contact.setMail(e.getContact().getMail());
                    etablissement.setContact(contact);
                    etablissementDiviseEvent.getEtablissements().add(etablissement);
                });
                return etablissementDiviseEvent;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

}
