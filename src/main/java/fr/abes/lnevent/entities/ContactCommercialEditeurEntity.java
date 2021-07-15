package fr.abes.lnevent.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ContactCommercialEditeur")
@NoArgsConstructor
@Getter
@Setter
public class ContactCommercialEditeurEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "contact_editeur_commercial_Sequence")
    @SequenceGenerator(name = "contact_editeur_commercial_Sequence", sequenceName = "CONTACT_EDITEUR_COMMERCIAL_SEQ", allocationSize = 1)
    public Long id;

    public String nomContactCommercial;

    public String prenomContactCommercial;

    public String mailContactCommercial;

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
