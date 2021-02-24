package fr.abes.lnevent.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.lnevent.dto.etablissement.EtablissementDTO;

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
    public EtablissementDTO convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, EtablissementDTO.class);
        } catch (IOException ex) {
            return null;
        }
    }

}
