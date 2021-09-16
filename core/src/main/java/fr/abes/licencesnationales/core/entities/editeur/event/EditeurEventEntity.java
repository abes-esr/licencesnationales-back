package fr.abes.licencesnationales.core.entities.editeur.event;

import fr.abes.licencesnationales.core.converter.contactediteur.ContactEditeurConverter;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.EventEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "EditeurEvent")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event", columnDefinition = "varchar(20)", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
public class EditeurEventEntity extends EventEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editeurevent_Sequence")
    @SequenceGenerator(name = "editeurevent_Sequence", sequenceName = "EDITEUREVENT_SEQ", allocationSize = 1)
    protected Integer id;

    @Column(name = "NOM_EDITEUR")
    protected String nomEditeur;

    @Column(name = "IDENTIFIANT_EDITEUR")
    protected String identifiantEditeur;

    @Column(name = "ADRESSE_EDITEUR")
    protected String adresseEditeur;

    @Lob
    @Column(name = "GROUPES_ETAB_RELIES")
    protected List<String> groupesEtabRelies = new ArrayList<>();

    @Lob
    @Convert(converter = ContactEditeurConverter.class)
    @Column(name = "LISTE_CONTACT_COMMERCIAL_EDITEURDTO", columnDefinition = "CLOB")
    protected Set<ContactEditeurDto> listeContactCommercialEditeur;

    @Lob
    @Convert(converter = ContactEditeurConverter.class)
    @Column(name = "LISTE_CONTACT_TECHNIQUE_EDITEURDTO", columnDefinition = "CLOB")
    protected Set<ContactEditeurDto> listeContactTechniqueEditeur;

    @Lob
    @Column(name = "ID_EDITEUR_FUSIONNES")
    protected List<Integer> idEditeurFusionnes;

    public EditeurEventEntity(Object source) {
        super(source);
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

        return id != null && id.equals(((EditeurEventEntity) obj).id);
    }


    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "EditeurEventEntity {" + "id=" + id + ", nom de l'éditeur=" + nomEditeur + " }";
    }
}
