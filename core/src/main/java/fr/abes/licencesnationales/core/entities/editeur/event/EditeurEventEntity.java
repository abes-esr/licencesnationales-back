package fr.abes.licencesnationales.core.entities.editeur.event;

import fr.abes.licencesnationales.core.converter.contactediteur.ContactEditeurConverter;
import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.EventEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "EditeurEvent")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event", columnDefinition = "varchar(20)", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
public class EditeurEventEntity extends EventEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editeurevent_Sequence")
    @SequenceGenerator(name = "editeurevent_Sequence", sequenceName = "EDITEUREVENT_SEQ", allocationSize = 1)
    protected Integer id = 0001;

    @Column(name = "NOM_EDITEUR")
    protected String nomEditeur = "event - nom editeur non renseigné";

    @Column(name = "IDENTIFIANT_EDITEUR")
    protected String identifiantEditeur = "event - identifiant editeur non renseigné";

    @Column(name = "ADRESSE_EDITEUR")
    protected String adresseEditeur = "event - adresse editeur non renseignée";

    private transient List<String> typesEtabs = new ArrayList<>();

    @Lob
    @Column(name = "GROUPES_ETAB_RELIES", columnDefinition = "CLOB")
    //protected byte[] typesEtabsInBdd =  new byte[1000];
    protected String typesEtabsInBdd = "event - types étab non renseignés";

    @Lob
    @Convert(converter = ContactEditeurConverter.class)
    @Column(name = "LISTE_CONTACT_COMMERCIAL_EDITEURDTO", columnDefinition = "CLOB")
    protected Set<ContactEditeurDto> listeContactCommercialEditeur = new HashSet<>();

    @Lob
    @Convert(converter = ContactEditeurConverter.class)
    @Column(name = "LISTE_CONTACT_TECHNIQUE_EDITEURDTO", columnDefinition = "CLOB")
    protected Set<ContactEditeurDto> listeContactTechniqueEditeur = new HashSet<>();


    public EditeurEventEntity(Object source, List<String> typesEtabs) {
        super(source);
        this.typesEtabs = typesEtabs;
    }

    @Deprecated
    public EditeurEventEntity() {
        super();
    }

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


    /*public void setTypesEtabsInBdd(List<String> typesEtabs) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        for (String element : typesEtabs) {
            out.writeUTF(element);
        }
        this.typesEtabsInBdd = baos.toByteArray();
    }*/

    public void setTypesEtabsInBdd(List<String> typesEtabs) throws IOException {
        String liste = "";
        for (String s : typesEtabs) {
            liste+=s;
        }
        this.typesEtabsInBdd = liste;

    }
}
