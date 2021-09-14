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
@DiscriminatorValue("commercial")
public class ContactCommercialEditeurEntity extends ContactEditeurEntity implements Serializable {

    /**
     * CTOR d'un contact commercial sans identifiant
     *
     * @param nomContactCommercial    Nom du contact
     * @param prenomContactCommercial Prénom du contact
     * @param mailContactCommercial   Email du contact
     */
    public ContactCommercialEditeurEntity(String nomContactCommercial, String prenomContactCommercial, String mailContactCommercial) {
        super(nomContactCommercial, prenomContactCommercial, mailContactCommercial);
    }

    /**
     * CTOR d'un contact commercial avec un identifiant
     *
     * @param id                      Identifiant du contact
     * @param nomContactCommercial    Nom du contact
     * @param prenomContactCommercial Prénom du contact
     * @param mailContactCommercial   Email du contact
     */
    public ContactCommercialEditeurEntity(Long id, String nomContactCommercial, String prenomContactCommercial, String mailContactCommercial) {
        super(id, nomContactCommercial, prenomContactCommercial, mailContactCommercial);
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

        return (id != null && id.equals(((ContactCommercialEditeurEntity) obj).id)) ||
                (id == null && nomContact.equals(((ContactCommercialEditeurEntity) obj).nomContact)
                        && prenomContact.equals(((ContactCommercialEditeurEntity) obj).prenomContact));
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "ContactCommercialEditeurEntity {" + "id=" + id + ", nom=" + nomContact + ", prénom=" + prenomContact + " }";
    }
}
