package fr.abes.lnevent.entities;

import fr.abes.lnevent.converter.JpaConverterJson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Editeur")
@NoArgsConstructor
@Getter @Setter
public class EditeurEntity {
    public EditeurEntity(Long id, String nom, String adresse, List<String> mailsPourBatch, List<String> mailPourInformation, Set<EtablissementEntity> etablissements) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.mailsPourBatch = mailsPourBatch;
        this.mailPourInformation = mailPourInformation;
        this.etablissements = etablissements;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    private Long id;

    private String nom;

    private String adresse;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<String> mailsPourBatch;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<String> mailPourInformation;

    @ManyToMany(mappedBy = "editeurs")
    private Set<EtablissementEntity> etablissements = new HashSet<>();
}
