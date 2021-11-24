package fr.abes.licencesnationales.core.converter;

import fr.abes.licencesnationales.core.dto.export.ExportEditeurDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementAdminDto;
import fr.abes.licencesnationales.core.dto.export.ExportIpDto;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementUserDto;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ExportConverter {
    @Autowired
    private UtilsMapper utilsMapper;

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
                for(IpEntity ip : entity.getIps()) {
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
                for(IpEntity ip : entity.getIps()) {
                    dto.ajouterIp(ip.getIp());
                }
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurExportAdmin() {
        Converter<EtablissementEntity, ExportEditeurDto> myConverter = new Converter<EtablissementEntity, ExportEditeurDto>() {

            public ExportEditeurDto convert(MappingContext<EtablissementEntity, ExportEditeurDto> context) {
                EtablissementEntity entity = context.getSource();
                ExportEditeurDto dto = new ExportEditeurDto();
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpExportAdmin() {
        Converter<EtablissementEntity, ExportIpDto> myConverter = new Converter<EtablissementEntity, ExportIpDto>() {

            public ExportIpDto convert(MappingContext<EtablissementEntity, ExportIpDto> context) {
                EtablissementEntity entity = context.getSource();
                ExportIpDto dto = new ExportIpDto();
                return dto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
