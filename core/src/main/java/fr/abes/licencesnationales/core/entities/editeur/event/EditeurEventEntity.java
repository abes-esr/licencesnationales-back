package fr.abes.licencesnationales.core.entities.editeur.event;

import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
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
    protected Integer id;

    @Column(name = "NOM_EDITEUR")
    protected String nom = "event - nom editeur non renseigné";

    @Column(name = "IDENTIFIANT_EDITEUR")
    protected String identifiant = "event - identifiant editeur non renseigné";

    @Column(name = "ADRESSE_EDITEUR")
    protected String adresse = "event - adresse editeur non renseignée";

    private transient Set<TypeEtablissementEntity> typesEtabs;

    //on empêche l'accès à cet attribut qui sera automatiquement mis à jour lors d'une mise à jour du set en transcient
    @Lob
    @Column(name = "TYPES_ETABLISSEMENTS")
    protected String typesEtabsInBdd = "event - types étab non renseignés";

    protected transient Set<ContactCommercialEditeurEntity> contactsCommerciaux;

    //on empêche l'accès à cet attribut qui sera automatiquement mis à jour lors d'une mise à jour du set en transcient
    @Lob
    @Column(name = "CONTACTS_COMMERCIAUX")
    private String contactsCommerciauxInBdd;

    protected transient Set<ContactTechniqueEditeurEntity> contactsTechniques;

    //on empêche l'accès à cet attribut qui sera automatiquement mis à jour lors d'une mise à jour du set en transcient
    @Lob
    @Column(name = "CONTACTS_TECHNIQUES")
    private String contactsTechniquesInBdd;

    @Deprecated
    public EditeurEventEntity() {
        super();
    }

    public EditeurEventEntity(Object source) {
        super(source);
        this.contactsTechniques = new HashSet<>();
        this.contactsCommerciaux = new HashSet<>();
        this.typesEtabs = new HashSet<>();
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
        return "EditeurEventEntity {" + "id=" + id + ", nom de l'éditeur=" + nom + " }";
    }


    public void addTypeEtab(TypeEtablissementEntity type) {
        this.typesEtabs.add(type);
    }


    public void addContactCommercial(ContactCommercialEditeurEntity contact) {
        this.contactsCommerciaux.add(contact);
    }

    public void addContactTechnique(ContactTechniqueEditeurEntity contact) {
        this.contactsTechniques.add(contact);
    }

}
