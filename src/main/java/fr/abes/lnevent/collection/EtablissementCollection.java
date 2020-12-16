package fr.abes.lnevent.collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Etablissement")
public class EtablissementCollection {

    public EtablissementCollection(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    public String id;

    public String name;
}
