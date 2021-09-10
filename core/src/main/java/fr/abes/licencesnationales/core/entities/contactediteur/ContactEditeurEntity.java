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
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_editeur_Sequence")
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

    public ContactEditeurEntity(String nomContact, String prenomContact, String mailContact) {
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
    }

    public ContactEditeurEntity(Long id, String nomContact, String prenomContact, String mailContact) {
        this.id = id;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
    }
}
