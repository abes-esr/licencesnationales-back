package fr.abes.licencesnationales.web.converter.ip;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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
        dto.setTypeIp(source.getClass().getSimpleName());
        dto.setTypeAcces((source.getIp().contains("-"))?"range":"ip");
        return dto;
    }
}
