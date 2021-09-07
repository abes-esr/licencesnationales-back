package fr.abes.licencesnationales.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Table(name = "ContactCommercialEditeur")
@NoArgsConstructor
@Getter
@Setter
public class ContactCommercialEditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_editeur_commercial_Sequence")
    @SequenceGenerator(name = "contact_editeur_commercial_Sequence", sequenceName = "CONTACT_EDITEUR_COMMERCIAL_SEQ", allocationSize = 1)
    private Long id;

    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le nom fourni n'est pas valide")
    private String nomContactCommercial;
    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le prénom fourni n'est pas valide")
    private String prenomContactCommercial;
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "L'adresse mail fournie n'est pas valide")
    private String mailContactCommercial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idEditeur", nullable = false)
    private EditeurEntity idEditeur;



    public ContactCommercialEditeurEntity(Long id, String nomContactCommercial, String prenomContactCommercial, String mailContactCommercial, EditeurEntity idEditeur) {

        this.id = id;
        this.nomContactCommercial = nomContactCommercial;
        this.prenomContactCommercial = prenomContactCommercial;
        this.mailContactCommercial = mailContactCommercial;
    }
}
