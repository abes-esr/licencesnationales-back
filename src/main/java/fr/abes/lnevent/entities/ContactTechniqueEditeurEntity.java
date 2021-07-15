package fr.abes.lnevent.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ContactEditeur")
@NoArgsConstructor
@Getter
@Setter
public class ContactTechniqueEditeurEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_editeur_Sequence")
    @SequenceGenerator(name = "contact_editeur_Sequence", sequenceName = "CONTACT_EDITEUR_SEQ", allocationSize = 1)
    public Long id;

    public String nomContactTechnique;

    public String prenomContactTechnique;

    public String mailContactTechnique;

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
