package fr.abes.licencesnationales.core.converter.etablissement;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.internal.Errors;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Cette classe recense tous les convertisseurs d'objet événement d'un établissement vers les objets d'entité
 */
@Component
public class EtablissementEntityConverter {

    @Autowired
    private UtilsMapper utilsMapper;

    @Autowired
    private TypeEtablissementRepository repository;

    /**
     * Bean de conversion d'un événement de création d'établissement
     */
    @Bean
    public void converterEtablissementCreeEvent() {
        Converter<EtablissementCreeEventEntity, EtablissementEntity> myConverter = new Converter<EtablissementCreeEventEntity, EtablissementEntity>() {

            @SneakyThrows
            public EtablissementEntity convert(MappingContext<EtablissementCreeEventEntity, EtablissementEntity> context) {
                EtablissementCreeEventEntity source = context.getSource();

                EtablissementEntity etablissementEntity = new EtablissementEntity();
                etablissementEntity.setName(source.getNomEtab());
                etablissementEntity.setSiren(source.getSiren());
                Optional<TypeEtablissementEntity> type = repository.findFirstByLibelle(source.getTypeEtablissement());
                if (!type.isPresent()) {
                    throw new Errors().errorMapping(source, context.getDestinationType(), new UnknownTypeEtablissementException("type d'établissement inconnu")).toMappingException();
                }
                etablissementEntity.setTypeEtablissement(type.get());
                etablissementEntity.setIdAbes(source.getIdAbes());

                ContactEntity contactEntity = new ContactEntity(source.getNomContact(),
                        source.getPrenomContact(),
                        source.getMailContact(),
                        source.getMotDePasse(),
                        source.getTelephoneContact(),
                        source.getAdresseContact(),
                        source.getBoitePostaleContact(),
                        source.getCodePostalContact(),
                        source.getCedexContact(),
                        source.getVilleContact(),
                        source.getRoleContact());

                etablissementEntity.setContact(contactEntity);

                return etablissementEntity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    /**
     * Bean de conversion d'un événement de modification d'établissement
     */
    @Bean
    public void converterEtablissementModifieEvent() {
        Converter<EtablissementModifieEventEntity, ContactEntity> myConverter = new Converter<EtablissementModifieEventEntity, ContactEntity>() {

            public ContactEntity convert(MappingContext<EtablissementModifieEventEntity, ContactEntity> context) {
                EtablissementModifieEventEntity source = context.getSource();

                ContactEntity contactEntity = new ContactEntity();
                contactEntity.setNom(source.getNomContact());
                contactEntity.setPrenom(source.getPrenomContact());
                contactEntity.setMail(source.getMailContact());
                contactEntity.setAdresse(source.getAdresseContact());
                contactEntity.setTelephone(source.getTelephoneContact());
                contactEntity.setBoitePostale(source.getBoitePostaleContact());
                contactEntity.setCodePostal(source.getCodePostalContact());
                contactEntity.setVille(source.getVilleContact());
                contactEntity.setCedex(source.getCedexContact());

                return contactEntity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    /**
     * Bean de conversion d'un événement de fusion d'établissement
     */
    @Bean
    public void converterEtablissementFusionneEvent() {
        Converter<EtablissementFusionneEventEntity, EtablissementEntity> myConverter = new Converter<EtablissementFusionneEventEntity, EtablissementEntity>() {

            @SneakyThrows
            public EtablissementEntity convert(MappingContext<EtablissementFusionneEventEntity, EtablissementEntity> context) {
                EtablissementFusionneEventEntity source = context.getSource();
                EtablissementEntity etablissementEntity = new EtablissementEntity();
                etablissementEntity.setName(source.getNomEtab());
                etablissementEntity.setSiren(source.getSiren());
                Optional<TypeEtablissementEntity> type = repository.findFirstByLibelle(source.getTypeEtablissement());
                if (!type.isPresent()) {
                    throw new Errors().errorMapping(source, context.getDestinationType(), new UnknownTypeEtablissementException("Type d'établissement inconnu")).toMappingException();
                }
                etablissementEntity.setTypeEtablissement(type.get());
                etablissementEntity.setIdAbes(source.getIdAbes());

                ContactEntity contactEntity = new ContactEntity();
                contactEntity.setNom(source.getNomContact());
                contactEntity.setPrenom(source.getPrenomContact());
                contactEntity.setMail(source.getMailContact());
                contactEntity.setAdresse(source.getAdresseContact());
                contactEntity.setTelephone(source.getTelephoneContact());
                contactEntity.setBoitePostale(source.getBoitePostaleContact());
                contactEntity.setCodePostal(source.getCodePostalContact());
                contactEntity.setVille(source.getVilleContact());
                contactEntity.setCedex(source.getCedexContact());

                etablissementEntity.setContact(contactEntity);
                return etablissementEntity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

    /**
     * Bean de conversion d'un événement de division d'établissement
     */
    @Bean
    public void converterEtablissementDiviseEvent() {
        Converter<EtablissementDiviseEventEntity, EtablissementEntity> myConverter = new Converter<EtablissementDiviseEventEntity, EtablissementEntity>() {

            @SneakyThrows
            public EtablissementEntity convert(MappingContext<EtablissementDiviseEventEntity, EtablissementEntity> context) {
                /** TODO : a revoir : copier / coller de la fusion */
                EtablissementDiviseEventEntity source = context.getSource();
                EtablissementEntity etablissementEntity = new EtablissementEntity();
                etablissementEntity.setName(source.getNomEtab());
                etablissementEntity.setSiren(source.getSiren());
                Optional<TypeEtablissementEntity> type = repository.findFirstByLibelle(source.getTypeEtablissement());
                if (!type.isPresent()) {
                    throw new Errors().errorMapping(source, context.getDestinationType(), new UnknownTypeEtablissementException("Type d'établissement inconnu")).toMappingException();
                }
                etablissementEntity.setTypeEtablissement(type.get());
                etablissementEntity.setIdAbes(source.getIdAbes());

                ContactEntity contactEntity = new ContactEntity();
                contactEntity.setNom(source.getNomContact());
                contactEntity.setPrenom(source.getPrenomContact());
                contactEntity.setMail(source.getMailContact());
                contactEntity.setAdresse(source.getAdresseContact());
                contactEntity.setTelephone(source.getTelephoneContact());
                contactEntity.setBoitePostale(source.getBoitePostaleContact());
                contactEntity.setCodePostal(source.getCodePostalContact());
                contactEntity.setVille(source.getVilleContact());
                contactEntity.setCedex(source.getCedexContact());

                etablissementEntity.setContact(contactEntity);
                return etablissementEntity;
            }
        };
        utilsMapper.addConverter(myConverter); //On ajoute le convertisseur à la liste des convertisseurs
    }

}
