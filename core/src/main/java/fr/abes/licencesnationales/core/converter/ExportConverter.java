package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.export.*;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
public class ExportConverter {
    private UtilsMapper utilsMapper;

    @Autowired
    public ExportConverter(UtilsMapper utilsMapper) {
        this.utilsMapper = utilsMapper;
    }

    @Bean
    public void converterEtablissementExportUser() {
        Converter<EtablissementEntity, ExportEtablissementUserDto> myConverter = new Converter<EtablissementEntity, ExportEtablissementUserDto>() {

            public ExportEtablissementUserDto convert(MappingContext<EtablissementEntity, ExportEtablissementUserDto> context) {
                EtablissementEntity entity = context.getSource();
                ExportEtablissementUserDto dto = new ExportEtablissementUserDto();
                dto.setIdAbes(entity.getIdAbes());
                dto.setSiren(entity.getSiren());
                dto.setNom(entity.getNom());
                dto.setTypeEtablissement(entity.getTypeEtablissement().getLibelle());
                StringBuilder adresseComplete = new StringBuilder(entity.getContact().getAdresse());
                adresseComplete.append(" ");
                adresseComplete.append(entity.getContact().getCodePostal());
                adresseComplete.append(" ");
                adresseComplete.append(entity.getContact().getVille());
                if (entity.getContact().getBoitePostale() != null && !entity.getContact().getBoitePostale().isEmpty()) {
                    adresseComplete.append(" ");
                    adresseComplete.append(entity.getContact().getBoitePostale());
                }
                if (entity.getContact().getCedex() != null && !entity.getContact().getCedex().isEmpty()) {
                    adresseComplete.append(" ");
                    adresseComplete.append(entity.getContact().getCedex());
                }
                dto.setAdresse(adresseComplete.toString());
                dto.setTelephone(entity.getContact().getTelephone());
                dto.setNomPrenomContact(entity.getContact().getNom() + " " + entity.getContact().getPrenom());
                dto.setMailContact(entity.getContact().getMail());
                for (IpEntity ip : entity.getIps()) {
                    if (ip.getStatut().getIdStatut() == 3)
                        dto.ajouterIp(ip.getIp());
                }
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEtablissementExportAdmin() {
        Converter<EtablissementEntity, ExportEtablissementAdminDto> myConverter = new Converter<EtablissementEntity, ExportEtablissementAdminDto>() {

            public ExportEtablissementAdminDto convert(MappingContext<EtablissementEntity, ExportEtablissementAdminDto> context) {
                EtablissementEntity entity = context.getSource();
                ExportEtablissementAdminDto dto = new ExportEtablissementAdminDto();
                dto.setIdAbes(entity.getIdAbes());
                dto.setSiren(entity.getSiren());
                dto.setNom(entity.getNom());
                dto.setTypeEtablissement(entity.getTypeEtablissement().getLibelle());
                StringBuilder adresseComplete = new StringBuilder(entity.getContact().getAdresse());
                adresseComplete.append(" ");
                adresseComplete.append(entity.getContact().getCodePostal());
                if (entity.getContact().getBoitePostale() != null && !entity.getContact().getBoitePostale().isEmpty()) {
                    adresseComplete.append(" ");
                    adresseComplete.append(entity.getContact().getBoitePostale());
                }
                if (entity.getContact().getCedex() != null && !entity.getContact().getCedex().isEmpty()) {
                    adresseComplete.append(" ");
                    adresseComplete.append(entity.getContact().getCedex());
                }
                dto.setAdresse(adresseComplete.toString());
                dto.setVille(entity.getContact().getVille());
                dto.setTelephone(entity.getContact().getTelephone());
                dto.setNomPrenomContact(entity.getContact().getNom() + " " + entity.getContact().getPrenom());
                dto.setMailContact(entity.getContact().getMail());
                for (IpEntity ip : entity.getIps()) {
                    if (ip.getStatut().getIdStatut() == 3)
                        dto.ajouterIp(ip.getIp());
                }
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurExportAdmin() {
        Converter<EditeurEntity, ExportEditeurDto> myConverter = new Converter<EditeurEntity, ExportEditeurDto>() {

            public ExportEditeurDto convert(MappingContext<EditeurEntity, ExportEditeurDto> context) {
                EditeurEntity entity = context.getSource();
                ExportEditeurDto dto = new ExportEditeurDto();
                dto.setId(entity.getIdentifiant());
                dto.setAdresse(entity.getAdresse());
                dto.setNom(entity.getNom());
                for (ContactEditeurEntity contactEditeurEntity : entity.getContactsCommerciaux()) {
                    ContactEditeurDto contactEditeurDto = new ContactEditeurDto();
                    contactEditeurDto.setMail(contactEditeurEntity.getMail());
                    contactEditeurDto.setNomPrenom(contactEditeurEntity.getNom() + " " + contactEditeurEntity.getPrenom());
                    contactEditeurDto.setType("Commercial");
                    dto.addContact(contactEditeurDto);
                }
                for (ContactEditeurEntity contactEditeurEntity : entity.getContactsTechniques()) {
                    ContactEditeurDto contactEditeurDto = new ContactEditeurDto();
                    contactEditeurDto.setMail(contactEditeurEntity.getMail());
                    contactEditeurDto.setNomPrenom(contactEditeurEntity.getNom() + " " + contactEditeurEntity.getPrenom());
                    contactEditeurDto.setType("Technique");
                    dto.addContact(contactEditeurDto);
                }
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpV4ExportUser() {
        Converter<IpV4, ExportIpDto> myConverter = new Converter<IpV4, ExportIpDto>() {

            public ExportIpDto convert(MappingContext<IpV4, ExportIpDto> context) {
                IpV4 entity = context.getSource();
                ExportIpDto dto = new ExportIpDto();
                if (entity.isRange()) {
                    dto.setType("Plage d'IP V4");
                    dto.setIp(entity.formatRange());
                } else {
                    dto.setType("IP V4");
                    dto.setIp(entity.getIp());
                }
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                dto.setDateCreation(format.format(entity.getDateCreation()));
                if(entity.getDateModification() != null) {
                    dto.setDateModificationStatut(format.format(entity.getDateModification()));
                }
                dto.setStatut(entity.getStatut().getLibelleStatut());
                dto.setCommentaire(entity.getCommentaires());
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpV6ExportUser() {
        Converter<IpV6, ExportIpDto> myConverter = new Converter<IpV6, ExportIpDto>() {

            public ExportIpDto convert(MappingContext<IpV6, ExportIpDto> context) {
                IpV6 entity = context.getSource();
                ExportIpDto dto = new ExportIpDto();
                if (entity.isRange()) {
                    dto.setType("Plage d'IP V6");
                    dto.setIp(entity.formatRange());
                } else {
                    dto.setType("IP V6");
                    dto.setIp(entity.getIp());
                }
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                dto.setDateCreation(format.format(entity.getDateCreation()));
                dto.setDateModificationStatut(format.format(entity.getDateModification()));
                dto.setStatut(entity.getStatut().getLibelleStatut());
                dto.setCommentaire(entity.getCommentaires());
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterExportEtablissementEditeur() {
        Converter<EtablissementEntity, ExportEtablissementEditeurDto> myConverter = new Converter<EtablissementEntity, ExportEtablissementEditeurDto>() {

            public ExportEtablissementEditeurDto convert(MappingContext<EtablissementEntity, ExportEtablissementEditeurDto> context) {
                EtablissementEntity etab = context.getSource();
                ExportEtablissementEditeurDto dto = new ExportEtablissementEditeurDto();
                dto.setIdEtablissement(etab.getIdAbes());
                dto.setSirenEtablissement(etab.getSiren());
                dto.setNomEtablissement(etab.getNom());
                dto.setTypeEtablissement(etab.getTypeEtablissement().getLibelle());
                dto.setAdresse(etab.getContact().getAdresse());
                dto.setBoitePostale(etab.getContact().getBoitePostale());
                dto.setCodePostal(etab.getContact().getCodePostal());
                dto.setCedex(etab.getContact().getCedex());
                dto.setVille(etab.getContact().getVille());
                dto.setNomContact(etab.getContact().getNom() + " " + etab.getContact().getPrenom());
                dto.setMailContact(etab.getContact().getMail());
                dto.setTelephoneContact(etab.getContact().getTelephone());
                etab.getIps().forEach(i -> dto.ajouterAcces(i.getIp()));
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }


    @Bean
    public void converterExportEtablissementFusionneEditeur() {
        Converter<EtablissementFusionneEventEntity, ExportEtablissementEditeurFusionDto> myConverter = new Converter<EtablissementFusionneEventEntity, ExportEtablissementEditeurFusionDto>() {

            public ExportEtablissementEditeurFusionDto convert(MappingContext<EtablissementFusionneEventEntity, ExportEtablissementEditeurFusionDto> context) {
                EtablissementFusionneEventEntity etab = context.getSource();
                ExportEtablissementEditeurFusionDto dto = new ExportEtablissementEditeurFusionDto();
                dto.setIdEtablissement(etab.getIdAbes());
                dto.setNomEtablissement(etab.getNomEtab());
                dto.setTypeEtablissement(etab.getTypeEtablissement());
                dto.setAdresse(etab.getAdresseContact());
                dto.setBoitePostale(etab.getBoitePostaleContact());
                dto.setCodePostal(etab.getCodePostalContact());
                dto.setCedex(etab.getCedexContact());
                dto.setVille(etab.getVilleContact());
                dto.setNomContact(etab.getNomContact() + " " + etab.getPrenomContact());
                dto.setMailContact(etab.getMailContact());
                dto.setTelephoneContact(etab.getTelephoneContact());
                dto.setSirensFusionnes(etab.getSirenAnciensEtablissements());
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
