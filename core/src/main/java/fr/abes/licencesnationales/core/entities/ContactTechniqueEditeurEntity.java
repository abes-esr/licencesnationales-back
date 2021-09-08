package fr.abes.licencesnationales.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Table(name = "ContactTechniqueEditeur")
@NoArgsConstructor
@Getter
@Setter
public class ContactTechniqueEditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_technique_editeur_Sequence")
    @SequenceGenerator(name = "contact_technique_editeur_Sequence", sequenceName = "CONTACT_TECHNIQUE_EDITEUR_SEQ", allocationSize = 1)
    private Long id;

    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le nom fourni n'est pas valide")
    private String nomContactTechnique;

    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le prénom fourni n'est pas valide")
    private String prenomContactTechnique;

    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "L'adresse mail fournie n'est pas valide")
    private String mailContactTechnique;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editeurEntity", nullable = false)
    @JsonIgnore
    private EditeurEntity editeurEntity;



    public ContactTechniqueEditeurEntity(Long id, String nomContactTechnique, String prenomContactTechnique, String mailContactTechnique) {

        this.id = id;
        this.nomContactTechnique = nomContactTechnique;
        this.prenomContactTechnique = prenomContactTechnique;
        this.mailContactTechnique = mailContactTechnique;
    }
}
