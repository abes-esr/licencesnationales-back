package fr.abes.lnevent.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Contact")
@NoArgsConstructor
@Getter @Setter
public class ContactEntity {

    public ContactEntity(Long id,
                         String nom,
                         String prenom,
                         String mail,
                         String telephone,
                         String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_Sequence")
    @SequenceGenerator(name = "contact_Sequence", sequenceName = "CONTACT_SEQ", allocationSize = 1)
    private Long id;

    private String nom;

    private String prenom;

    private String mail;

    private String telephone;

    private String adresse;

}
