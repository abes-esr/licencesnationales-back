package fr.abes.licencesnationales.web.converter.ip;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import fr.abes.licencesnationales.web.dto.ip.creation.Ipv4AjouteeWebDto;
import fr.abes.licencesnationales.web.dto.ip.creation.Ipv6AjouteeWebDto;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
public class IpWebDtoConverter {
    @Autowired
    private UtilsMapper utilsMapper;

    @Bean
    public void converterIpV4IpWebDto() {
        Converter<IpV4, IpWebDto> myConverter = new Converter<IpV4, IpWebDto>() {

            public IpWebDto convert(MappingContext<IpV4, IpWebDto> context) {
                IpV4 source = context.getSource();

                return getDto(source);

            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpV6IpWebDto() {
        Converter<IpV6, IpWebDto> myConverter = new Converter<IpV6, IpWebDto>() {

            public IpWebDto convert(MappingContext<IpV6, IpWebDto> context) {
                IpV6 source = context.getSource();
                return getDto(source);
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    private IpWebDto getDto(IpEntity source) {
        IpWebDto dto = new IpWebDto(source.getId(), source.getIp(), source.getStatut().getLibelleStatut(), source.getDateCreation(), source.getDateModification(), source.getCommentaires());
        dto.setTypeIp(source.getClass().getSimpleName().toUpperCase(Locale.ROOT));
        dto.setTypeAcces((source.getIp().contains("-")) ? "range" : "ip");
        return dto;
    }

    @Bean
    public void converterIpv4AjouteeWebDtoIpCreeEvent() {

        Converter<Ipv4AjouteeWebDto, IpCreeEventEntity> myConverter = new Converter<Ipv4AjouteeWebDto, IpCreeEventEntity>() {

            public IpCreeEventEntity convert(MappingContext<Ipv4AjouteeWebDto, IpCreeEventEntity> context) {
                try {
                    Ipv4AjouteeWebDto source = context.getSource();
                    if (source.getIp() == null) {
                        throw new IllegalArgumentException("Le champs 'ip' est obligatoire");
                    }
                    IpCreeEventEntity eventEntity = new IpCreeEventEntity(this, source.getIp(), source.getCommentaires());
                    eventEntity.setTypeAcces((source.getIp().contains("-")) ? "range" : "ip");
                    eventEntity.setTypeIp(IpType.IPV4);
                    return eventEntity;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }

    @Bean
    public void converterIpv6AjouteeWebDtoIpCreeEvent() {

        Converter<Ipv6AjouteeWebDto, IpCreeEventEntity> myConverter = new Converter<Ipv6AjouteeWebDto, IpCreeEventEntity>() {

            public IpCreeEventEntity convert(MappingContext<Ipv6AjouteeWebDto, IpCreeEventEntity> context) {
                try {
                    Ipv6AjouteeWebDto source = context.getSource();
                    if (source.getIp() == null) {
                        throw new IllegalArgumentException("Le champs 'ip' est obligatoire");
                    }
                    IpCreeEventEntity eventEntity = new IpCreeEventEntity(this, source.getIp(), source.getCommentaires());
                    eventEntity.setTypeAcces((source.getIp().contains("-")) ? "range" : "ip");
                    eventEntity.setTypeIp(IpType.IPV6);
                    return eventEntity;
                } catch (IllegalArgumentException ex) {
                    throw new MappingException(Arrays.asList(new ErrorMessage(ex.getMessage())));
                }
            }
        };
        utilsMapper.addConverter(myConverter);
    }
}
