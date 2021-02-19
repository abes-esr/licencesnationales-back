package fr.abes.lnevent.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Entity
@Table(name = "Contact")
@NoArgsConstructor
@Getter @Setter
public class ContactRow {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_Sequence")
    @SequenceGenerator(name = "contact_Sequence", sequenceName = "CONTACT_SEQ")
    public Long id;
    public String nom;
    public String prenom;
    public String mail;
    public String motDePasse;
    public String telephone;
    public String adresse;
    public String siren;
    public String role;


    public ContactRow(Long id,
                      String nom,
                      String prenom,
                      String mail,
                      String motDePasse,
                      String telephone,
                      String adresse,
                      String siren,
                      String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.siren = siren;
        this.role = role;
    }
}

