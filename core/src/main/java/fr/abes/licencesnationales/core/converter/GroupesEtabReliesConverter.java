package fr.abes.licencesnationales.core.converter;


import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupesEtabReliesConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ";";
    private ArrayList emptyList = new ArrayList<>();

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(SPLIT_CHAR)) : emptyList();
    }

    public ArrayList emptyList(){
        return this.emptyList;
    }
}

