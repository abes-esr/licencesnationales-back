package fr.abes.licencesnationales.core.converter.ip;

import com.github.jgonian.ipmath.Ipv6Range;

import javax.persistence.AttributeConverter;

/**
 * Ce convertisseur permet de gérer les entrées/sorties des objets Ipv6Range en base de données.
 * Les objets Ipv6Range sont stockés sous la forme d'une chaîne de caractère.
 * Ce convertisseur permet donc de convertir les objets Ipv6Range en chaîne de caractère et vice versa.
 */
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