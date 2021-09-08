package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementFusionneEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EntityConverter {
    @Autowired
    private UtilsMapper utilsMapper;

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

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));
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
        utilsMapper.addConverter(myConverter);
    }

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

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(source.getListeContactCommercialEditeur(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(source.getListeContactTechniqueEditeur(), ContactTechniqueEditeurEntity.class));
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }


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
        utilsMapper.addConverter(myConverter);
    }

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
        utilsMapper.addConverter(myConverter);
    }

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
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpAjouteeEvent() {
        Converter<IpAjouteeEvent, IpEntity> myConverter = new Converter<IpAjouteeEvent, IpEntity>() {

            public IpEntity convert(MappingContext<IpAjouteeEvent, IpEntity> context) {
                IpAjouteeEvent ipAjouteeEvent = context.getSource();
                IpEntity entity = new IpEntity(null, ipAjouteeEvent.getIp(), ipAjouteeEvent.getTypeAcces(), ipAjouteeEvent.getTypeIp(), ipAjouteeEvent.getCommentaires());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpModifieeEvent() {
        Converter<IpModifieeEvent, IpEntity> myConverter = new Converter<IpModifieeEvent, IpEntity>() {

            public IpEntity convert(MappingContext<IpModifieeEvent, IpEntity> context) {
                IpModifieeEvent ipModifieeEvent = context.getSource();
                IpEntity entity = new IpEntity();
                entity.setId(ipModifieeEvent.getId());
                entity.setIp(ipModifieeEvent.getIp());
                entity.setValidee(ipModifieeEvent.isValidee());
                entity.setDateModification(new Date());
                entity.setTypeAcces(ipModifieeEvent.getTypeAcces());
                entity.setTypeIp(ipModifieeEvent.getTypeIp());
                entity.setCommentaires(ipModifieeEvent.getCommentaires());
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
