package fr.abes.lnevent.entities;

import fr.abes.lnevent.converter.JpaConverterJson;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Editeur")
@NoArgsConstructor
public class EditeurEntity {
    public EditeurEntity(Long id, String nom, String adresse, List<String> mailsPourBatch, List<String> mailPourInformation) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.mailsPourBatch = mailsPourBatch;
        this.mailPourInformation = mailPourInformation;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    public Long id;

    public String nom;

    public String adresse;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public List<String> mailsPourBatch;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public List<String> mailPourInformation;
}
