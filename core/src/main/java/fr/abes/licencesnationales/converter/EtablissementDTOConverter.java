package fr.abes.licencesnationales.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.dto.etablissement.EtablissementDto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter()
public class EtablissementDTOConverter implements AttributeConverter<Object, String> {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public EtablissementDto convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, EtablissementDto.class);
        } catch (IOException ex) {
            return null;
        }
    }

}
