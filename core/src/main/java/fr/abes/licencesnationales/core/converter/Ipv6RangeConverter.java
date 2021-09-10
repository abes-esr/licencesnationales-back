package fr.abes.licencesnationales.core.converter;

import com.github.jgonian.ipmath.Ipv6Range;

import javax.persistence.AttributeConverter;

public class Ipv6RangeConverter implements AttributeConverter<Ipv6Range, String> {

    @Override
    public String convertToDatabaseColumn(Ipv6Range attr) {

        if (attr == null) {
            return null;
        }

       return attr.toString();
    }

    @Override
    public Ipv6Range convertToEntityAttribute(String dbData) {

        if (dbData == null) {
            return null;
        }

        return Ipv6Range.parse(dbData);
    }
}