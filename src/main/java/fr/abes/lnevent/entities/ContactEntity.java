package fr.abes.lnevent.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Contact")
@NoArgsConstructor
public class ContactEntity {

    public ContactEntity(Long id, String nom, String prenom, String mail, String telephone, String adresse, String boitePostale, String codePostal, String cedex, String ville) {
    public ContactEntity(Long id,
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
        this.boitePostale = boitePostale;
        this.codePostal = codePostal;
        this.cedex = cedex;
        this.ville = ville;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_Sequence")
    @SequenceGenerator(name = "contact_Sequence", sequenceName = "CONTACT_SEQ", allocationSize = 1)
    public Long id;

    public String nom;

    public String prenom;

    public String mail;

    public String motDePasse;

    public String telephone;

    public String adresse;

    public String siren;

}
