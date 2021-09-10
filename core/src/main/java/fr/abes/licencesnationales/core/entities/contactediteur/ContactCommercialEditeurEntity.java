package fr.abes.licencesnationales.core.entities.contactediteur;

import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
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

    public ContactCommercialEditeurEntity(String nomContactCommercial, String prenomContactCommercial, String mailContactCommercial) {
        super(nomContactCommercial, prenomContactCommercial, mailContactCommercial);
    }

    public ContactCommercialEditeurEntity(Long id, String nomContactCommercial, String prenomContactCommercial, String mailContactCommercial, EditeurEntity idEditeur) {
        super(id, nomContactCommercial, prenomContactCommercial, mailContactCommercial);
    }
}
