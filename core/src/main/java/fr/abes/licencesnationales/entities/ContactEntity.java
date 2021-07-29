package fr.abes.licencesnationales.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Contact")
@NoArgsConstructor
@Getter
@Setter
public class ContactEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_Sequence")
    @SequenceGenerator(name = "contact_Sequence", sequenceName = "CONTACT_SEQ", allocationSize = 1)
    private Long id;

    private String nom;

    private String prenom;

    private String mail;

    private String motDePasse;

    private String telephone;

    private String adresse;

    private String boitePostale;

    private String codePostal;

    private String cedex;

    private String ville;

    private  String role;


    public ContactEntity(Long id, String nom, String prenom, String mail, String motDePasse, String telephone, String adresse, String boitePostale, String codePostal, String cedex, String ville, String role) {

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
        this.boitePostale = boitePostale;
        this.codePostal = codePostal;
        this.cedex = cedex;
        this.ville = ville;
        this.role = role;
    }
}
