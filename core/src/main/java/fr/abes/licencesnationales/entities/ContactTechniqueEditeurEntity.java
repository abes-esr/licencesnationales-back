package fr.abes.licencesnationales.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    private String nomContactTechnique;

    private String prenomContactTechnique;

    private String mailContactTechnique;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editeur_id", nullable = false)
    private EditeurEntity editeurEntity;



    public ContactTechniqueEditeurEntity(Long id, String nomContactTechnique, String prenomContactTechnique, String mailContactTechnique) {

        this.id = id;
        this.nomContactTechnique = nomContactTechnique;
        this.prenomContactTechnique = prenomContactTechnique;
        this.mailContactTechnique = mailContactTechnique;
    }
}
