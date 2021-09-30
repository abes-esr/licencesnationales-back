package fr.abes.licencesnationales.core.converter;


import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
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
        log.info("liste typeEtab = " + this.emptyList);
        return this.emptyList;
    }
}

