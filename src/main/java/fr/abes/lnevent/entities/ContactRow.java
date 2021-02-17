package fr.abes.lnevent.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Contact")
public class ContactRow {

    public ContactRow(String id,
                      String nom,
                      String prenom,
                      String mail,
                      String motDePasse,
                      String telephone,
                      String adresse,
                      String siren) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.siren = siren;
    }

    @Id
    public String id;

    public String nom;

    public String prenom;

    public String mail;

    public String motDePasse;

    public String telephone;

    public String adresse;

    public String siren;

}
