package fr.abes.licencesnationales.core.converter.etablissement;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementFusionneEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Cette classe recense tous les convertisseurs d'objet événement d'un établissement vers les objets d'entité
 */
@Component
public class EtablissementEntityConverter {

    @Autowired
    private UtilsMapper utilsMapper;

    /**
     * Bean de conversion d'un événement de création d'établissement
     */
    @Bean
    public void converterEtablissementCreeEvent() {
        Converter<EtablissementCreeEvent, EtablissementEntity> myConverter = new Converter<EtablissementCreeEvent, EtablissementEntity>() {

            public EtablissementEntity convert(MappingContext<EtablissementCreeEvent, EtablissementEntity> context) {
                EtablissementCreeEvent source = context.getSource();

                EtablissementEntity etablissementEntity = new EtablissementEntity();
                etablissementEntity.setName(source.getNom());
                etablissementEntity.setSiren(source.getSiren());
                etablissementEntity.setTypeEtablissement(source.getTypeEtablissement());
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
        Converter<EtablissementModifieEvent, ContactEntity> myConverter = new Converter<EtablissementModifieEvent, ContactEntity>() {

            public ContactEntity convert(MappingContext<EtablissementModifieEvent, ContactEntity> context) {
                EtablissementModifieEvent source = context.getSource();

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
        Converter<EtablissementFusionneEvent, EtablissementEntity> myConverter = new Converter<EtablissementFusionneEvent, EtablissementEntity>() {

            public EtablissementEntity convert(MappingContext<EtablissementFusionneEvent, EtablissementEntity> context) {
                EtablissementFusionneEvent source = context.getSource();
                EtablissementEntity etablissementEntity = new EtablissementEntity();
                etablissementEntity.setName(source.getNom());
                etablissementEntity.setSiren(source.getSiren());
                etablissementEntity.setTypeEtablissement(source.getTypeEtablissement());
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
