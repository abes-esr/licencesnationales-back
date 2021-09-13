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

    public ContactTechniqueEditeurEntity(String nomContactTechnique, String prenomContactTechnique, String mailContactTechnique) {
        super(nomContactTechnique, prenomContactTechnique, mailContactTechnique);
    }

    public ContactTechniqueEditeurEntity(Long id, String nomContactTechnique, String prenomContactTechnique, String mailContactTechnique) {
        super(id, nomContactTechnique, prenomContactTechnique, mailContactTechnique);
    }
}
