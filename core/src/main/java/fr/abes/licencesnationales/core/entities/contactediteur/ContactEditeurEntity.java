package fr.abes.licencesnationales.core.entities.contactediteur;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING,
        columnDefinition = "VARCHAR(20)")
@Table(name = "ContactEditeur")
@DiscriminatorOptions(force = true)
@NoArgsConstructor
@Getter
@Setter
public abstract class ContactEditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_editeur_Sequence")
    @SequenceGenerator(name = "contact_editeur_Sequence", sequenceName = "CONTACT_EDITEUR_SEQ", allocationSize = 1)
    protected Long id;

    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le nom fourni n'est pas valide")
    protected String nomContact;
    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le prénom fourni n'est pas valide")
    protected String prenomContact;
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "L'adresse mail fournie n'est pas valide")
    protected String mailContact;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editeur", nullable = false)
    @JsonIgnore
    protected EditeurEntity editeurEntity;

    /**
     * CTOR d'un contact d'éditeur sans identifiant
     *
     * @param nomContact Nom du contact
     * @param prenomContact Prénom du contact
     * @param mailContact Email du contact
     */
    public ContactEditeurEntity(String nomContact, String prenomContact, String mailContact) {
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
    }

    /**
     * CTOR d'un contact d'éditeur avec identifiant
     *
     * @param id Identifiant du contact
     * @param nomContact Nom du contact
     * @param prenomContact Prénom du contact
     * @param mailContact Email du contact
     */
    public ContactEditeurEntity(Long id, String nomContact, String prenomContact, String mailContact) {
        this.id = id;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
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
                (id == null && nomContact.equals(((ContactEditeurEntity) obj).nomContact)
                        && prenomContact.equals(((ContactEditeurEntity) obj).prenomContact));
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "ContactEditeurEntity {" + "id=" + id + ", nom=" + nomContact + ", prénom=" + prenomContact + " }";
    }
}
