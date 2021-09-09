package fr.abes.licencesnationales.core.converter;

import com.github.jgonian.ipmath.Ipv4Range;

import javax.persistence.AttributeConverter;

public class Ipv4RangeConverter implements AttributeConverter<Ipv4Range, String> {

    @Override
    public String convertToDatabaseColumn(Ipv4Range attr) {

        if (attr == null) {
            return null;
        }

       return attr.toString();
    }

    @Override
    public Ipv4Range convertToEntityAttribute(String dbData) {

        if (dbData == null) {
            return null;
        }

        return Ipv4Range.parse(dbData);
    }
}