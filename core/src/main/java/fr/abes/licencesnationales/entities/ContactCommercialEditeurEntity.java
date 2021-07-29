package fr.abes.licencesnationales.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    private String nomContactCommercial;

    private String prenomContactCommercial;

    private String mailContactCommercial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editeur_id", nullable = false)
    private EditeurEntity editeurEntity;



    public ContactCommercialEditeurEntity(Long id, String nomContactCommercial, String prenomContactCommercial, String mailContactCommercial) {

        this.id = id;
        this.nomContactCommercial = nomContactCommercial;
        this.prenomContactCommercial = prenomContactCommercial;
        this.mailContactCommercial = mailContactCommercial;
    }
}
