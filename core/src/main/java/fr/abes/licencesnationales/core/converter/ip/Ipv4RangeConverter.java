package fr.abes.licencesnationales.core.converter.ip;

import com.github.jgonian.ipmath.Ipv4Range;

import javax.persistence.AttributeConverter;

/**
 * Ce convertisseur permet de gérer les entrées/sorties des objets Ipv4Range en base de données.
 * Les objets Ipv4Range sont stockés sous la forme d'une chaîne de caractère.
 * Ce convertisseur permet donc de convertir les objets Ipv4Range en chaîne de caractère et vice versa.
 */
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