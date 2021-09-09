package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.editeur.*;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.entities.*;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.core.exception.IpException;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class EntityConverter {
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
                entity.setDateCreation(dto.getDateCreation());

                Set<ContactCommercialEditeurDto> listeCCdto = dto.getListeContactCommercialEditeurDto();
                Set<ContactTechniqueEditeurDto> listeCTdto = dto.getListeContactTechniqueEditeurDto();
                Set<ContactCommercialEditeurEntity> listeCC = new HashSet<>();
                Set<ContactTechniqueEditeurEntity> listeCT = new HashSet<>();


                for (ContactCommercialEditeurDto ccDto : listeCCdto) {
                    ContactCommercialEditeurEntity ce = utilsMapper.map(ccDto, ContactCommercialEditeurEntity.class);
                    ce.setEditeurEntity(entity);
                    listeCC.add(ce);
                }
                for (ContactTechniqueEditeurDto ctDto : listeCTdto) {
                    ContactTechniqueEditeurEntity ct = utilsMapper.map(ctDto, ContactTechniqueEditeurEntity.class);
                    ct.setEditeurEntity(entity);
                    listeCT.add(ct);
                }

                entity.setContactCommercialEditeurEntities(listeCC);
                entity.setContactTechniqueEditeurEntities(listeCT);
                return entity;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterContactCommercialEditeurEntity() {
        Converter<ContactCommercialEditeurDto, ContactCommercialEditeurEntity> myConverter = new Converter<ContactCommercialEditeurDto, ContactCommercialEditeurEntity>() {

            @Override
            public ContactCommercialEditeurEntity convert(MappingContext<ContactCommercialEditeurDto, ContactCommercialEditeurEntity> context) {

                ContactCommercialEditeurDto source = context.getSource();
                ContactCommercialEditeurEntity entity = new ContactCommercialEditeurEntity();
                entity.setNomContactCommercial(source.getNomContactCommercial());
                entity.setPrenomContactCommercial(source.getPrenomContactCommercial());
                entity.setMailContactCommercial(source.getMailContactCommercial());

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
                entity.setIdEditeur(dto.getId());
                entity.setNomEditeur(dto.getNomEditeur());
                entity.setIdentifiantEditeur(dto.getIdentifiantEditeur());
                entity.setAdresseEditeur(dto.getAdresseEditeur());
                entity.setGroupesEtabRelies(dto.getGroupesEtabRelies());

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(dto.getListeContactCommercialEditeurDto(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(dto.getListeContactTechniqueEditeurDto(), ContactTechniqueEditeurEntity.class));
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

                entity.setContactCommercialEditeurEntities(utilsMapper.mapSet(dto.getListeContactCommercialEditeurDto(), ContactCommercialEditeurEntity.class));
                entity.setContactTechniqueEditeurEntities(utilsMapper.mapSet(dto.getListeContactTechniqueEditeurDto(), ContactTechniqueEditeurEntity.class));
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
                EtablissementCreeDto dto = source.getEtablissement();

                EtablissementEntity etablissementEntity = new EtablissementEntity();
                etablissementEntity.setName(dto.getEtablissementDTO().getNom());
                etablissementEntity.setSiren(dto.getEtablissementDTO().getSiren());
                etablissementEntity.setTypeEtablissement(dto.getEtablissementDTO().getTypeEtablissement());
                etablissementEntity.setIdAbes(dto.getEtablissementDTO().getIdAbes());

                ContactEntity contactEntity = new ContactEntity(dto.getEtablissementDTO().getNomContact(),
                        dto.getEtablissementDTO().getPrenomContact(),
                        dto.getEtablissementDTO().getMailContact(),
                        dto.getEtablissementDTO().getMotDePasse(),
                        dto.getEtablissementDTO().getTelephoneContact(),
                        dto.getEtablissementDTO().getAdresseContact(),
                        dto.getEtablissementDTO().getBoitePostaleContact(),
                        dto.getEtablissementDTO().getCodePostalContact(),
                        dto.getEtablissementDTO().getCedexContact(),
                        dto.getEtablissementDTO().getVilleContact(),
                        dto.getEtablissementDTO().getRoleContact());

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
    public void converterEtablissementDto() {
        Converter<EtablissementDto, EtablissementEntity> myConverter = new Converter<EtablissementDto, EtablissementEntity>() {

            public EtablissementEntity convert(MappingContext<EtablissementDto, EtablissementEntity> context) {
                EtablissementDto source = context.getSource();

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
    public void converterIpAjouteeEvent() {
        Converter<IpAjouteeEvent, IpEntity> myConverter = new Converter<IpAjouteeEvent, IpEntity>() {

            public IpEntity convert(MappingContext<IpAjouteeEvent, IpEntity> context) {
                try {
                    IpAjouteeEvent ipAjouteeEvent = context.getSource();
                    IpEntity entity;
                    if (ipAjouteeEvent.getTypeIp() == IpType.IPV4) {
                        entity = new IpV4(ipAjouteeEvent.getIp(), ipAjouteeEvent.getCommentaires());
                    } else if (ipAjouteeEvent.getTypeIp() == IpType.IPV6) {
                        entity = new IpV6(ipAjouteeEvent.getIp(), ipAjouteeEvent.getCommentaires());
                    } else {
                        throw new UnsupportedOperationException("Le type IP n'est pas supporté : " + ipAjouteeEvent.getTypeIp());
                    }

                    return entity;
                } catch (IpException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpModifieeEvent() {
        Converter<IpModifieeEvent, IpEntity> myConverter = new Converter<IpModifieeEvent, IpEntity>() {

            public IpEntity convert(MappingContext<IpModifieeEvent, IpEntity> context) {
                try {
                    IpModifieeEvent ipModifieeEvent = context.getSource();
                    IpEntity entity;
                    if (ipModifieeEvent.getTypeIp() == IpType.IPV4) {
                        entity = new IpV4(ipModifieeEvent.getId(), ipModifieeEvent.getIp(), ipModifieeEvent.getCommentaires());
                    } else if (ipModifieeEvent.getTypeIp() == IpType.IPV6) {
                        entity = new IpV6(ipModifieeEvent.getId(), ipModifieeEvent.getIp(), ipModifieeEvent.getCommentaires());
                    } else {
                        throw new UnsupportedOperationException("Le type IP n'est pas supporté : " + ipModifieeEvent.getTypeIp());
                    }

                    return entity;
                } catch (IpException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
