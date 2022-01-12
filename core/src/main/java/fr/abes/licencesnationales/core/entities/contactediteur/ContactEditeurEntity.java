package fr.abes.licencesnationales.core.entities.contactediteur;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING,
        columnDefinition = "VARCHAR(20)")
@Table(name = "ContactEditeur")
@DiscriminatorOptions(force = true)
@Getter
@Setter
public abstract class ContactEditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_editeur_Sequence")
    @SequenceGenerator(name = "contact_editeur_Sequence", sequenceName = "CONTACT_EDITEUR_SEQ", allocationSize = 1)
    protected Integer id;

    @NotBlank(message = "Le nom fourni n'est pas valide")
    protected String nom;
    @NotBlank(message = "Le prénom fourni n'est pas valide")
    protected String prenom;

    @NotNull
    @Email
    @Pattern(regexp = ".+@.+\\..+", message = "L'adresse mail fournie n'est pas valide")
    protected String mail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = EditeurEntity.class)
    @JoinColumn(name = "editeur", nullable = false)
    @JsonIgnore
    protected EditeurEntity editeur;

    /**
     * CTOR vide utilisé par JPA
     * on évite la null pointer exception en initialisant les attributs
     *
     */
    public ContactEditeurEntity() {
    }

    /**
     * CTOR d'un contact d'éditeur sans identifiant
     *
     * @param nom Nom du contact
     * @param prenom Prénom du contact
     * @param mail Email du contact
     */
    public ContactEditeurEntity(String nom, String prenom, String mail) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
    }

    /**
     * CTOR d'un contact d'éditeur avec identifiant
     *
     * @param id Identifiant du contact
     * @param nom Nom du contact
     * @param prenom Prénom du contact
     * @param mail Email du contact
     */
    public ContactEditeurEntity(Integer id, String nom, String prenom, String mail) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return (id != null && id.equals(((ContactEditeurEntity) obj).id)) ||
                (id == null && nom.equals(((ContactEditeurEntity) obj).nom)
                        && prenom.equals(((ContactEditeurEntity) obj).prenom));
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "ContactEditeurEntity {" + "id=" + id + ", nom=" + nom + ", prénom=" + prenom + " }";
    }
}
