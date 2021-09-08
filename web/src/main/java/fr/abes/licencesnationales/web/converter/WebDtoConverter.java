package fr.abes.licencesnationales.web.converter;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.editeur.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.web.dto.editeur.EditeurCreeWebDto;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class WebDtoConverter {


    @Autowired
    UtilsMapper utilsMapper;


    @Bean
    public void converterEditeurCreeDto() {
        Converter<EditeurCreeWebDto, EditeurCreeDto> myConverter = new Converter<EditeurCreeWebDto, EditeurCreeDto>() {

            public EditeurCreeDto convert(MappingContext<EditeurCreeWebDto, EditeurCreeDto> context) {
                EditeurCreeWebDto source = context.getSource();

                EditeurCreeDto editeurCreeDto = new EditeurCreeDto();
                editeurCreeDto.setNomEditeur(source.getNomEditeur());
                editeurCreeDto.setIdentifiantEditeur(source.getIdentifiantEditeur());
                editeurCreeDto.setAdresseEditeur(source.getAdresseEditeur());
                editeurCreeDto.setGroupesEtabRelies(source.getGroupesEtabRelies());

                editeurCreeDto.setListeContactCommercialEditeurDto(utilsMapper.mapSet(source.getListeContactCommercialEditeurWebDto(), ContactCommercialEditeurDto.class));
                editeurCreeDto.setListeContactTechniqueEditeurDto(utilsMapper.mapSet(source.getListeContactTechniqueEditeurWebDto(), ContactTechniqueEditeurDto.class));

                return editeurCreeDto;
            }
        };
        utilsMapper.addConverter(myConverter);
    }


}
