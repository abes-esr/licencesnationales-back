package fr.abes.licencesnationales.core.converter.contactediteur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class ContactEditeurConverter implements AttributeConverter<Object, String> {
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
    public ContactEditeurDto convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ContactEditeurDto.class);
        } catch (IOException ex) {
            return null;
        }
    }
}
