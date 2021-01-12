package fr.abes.lnevent.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Contact")
public class ContactRow {

    public ContactRow(String id,
                      String nom,
                      String prenom,
                      String mail,
                      String telephone,
                      String adresse,
                      String idEtablissement) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.telephone = telephone;
        this.adresse = adresse;
        this.idEtablissement = idEtablissement;
    }

    @Id
    public String id;

    public String nom;

    public String prenom;

    public String mail;

    public String telephone;

    public String adresse;

    public String idEtablissement;

}
