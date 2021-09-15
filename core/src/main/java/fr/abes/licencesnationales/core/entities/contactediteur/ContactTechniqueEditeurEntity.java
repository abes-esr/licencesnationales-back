package fr.abes.licencesnationales.core.entities.contactediteur;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;


@Entity
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("technique")
public class ContactTechniqueEditeurEntity extends ContactEditeurEntity implements Serializable {

    /**
     * CTOR d'un contact technique sans identifiant
     *
     * @param nomContactTechnique Nom du contact
     * @param prenomContactTechnique Prénom du contact
     * @param mailContactTechnique Email du contact
     */
    public ContactTechniqueEditeurEntity(String nomContactTechnique, String prenomContactTechnique, String mailContactTechnique) {
        super(nomContactTechnique, prenomContactTechnique, mailContactTechnique);
    }

    /**
     * CTOR d'un contact technique avec un identifiant.
     *
     * @param id Identifiant du contact
     * @param nomContactTechnique Nom du contact
     * @param prenomContactTechnique Prénom du contact
     * @param mailContactTechnique Email du contact
     */
    public ContactTechniqueEditeurEntity(Integer id, String nomContactTechnique, String prenomContactTechnique, String mailContactTechnique) {
        super(id, nomContactTechnique, prenomContactTechnique, mailContactTechnique);
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

        return (id != null && id.equals(((ContactTechniqueEditeurEntity) obj).id)) ||
                (id == null && nomContact.equals(((ContactTechniqueEditeurEntity) obj).nomContact)
                        && prenomContact.equals(((ContactTechniqueEditeurEntity) obj).prenomContact));
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "ContactTechniqueEditeurEntity {" + "id=" + id + ", nom=" + nomContact + ", prénom=" + prenomContact + " }";
    }
}
