package fr.abes.lnevent.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Etablissement")
public class EtablissementRow {

    public EtablissementRow(String id, String name, String adresse, String siren, String typeEtablissement, String motDePasse, String idAbes) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.motDePasse = motDePasse;
        this.idAbes = idAbes;
    }

    @Id
    public String id;

    public String name;

    private String adresse;

    private String siren;

    private String typeEtablissement;

    private String motDePasse;

    private String idAbes;
}
