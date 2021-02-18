package fr.abes.lnevent.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "CONTACT_LN_EVENT")
@Getter @Setter
@NoArgsConstructor
public class ContactRow {

    public ContactRow(String id,
                      String nom,
                      String prenom,
                      String mail,
                      String motDePasse,
                      String telephone,
                      String adresse,
                      String siren,
                      Boolean isAdmin) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.siren = siren;
        this.isAdmin = isAdmin;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String id;

    public String nom;

    public String prenom;

    public String mail;

    public String motDePasse;

    public String telephone;

    public String adresse;

    public String siren;

    public Boolean isAdmin;

}
