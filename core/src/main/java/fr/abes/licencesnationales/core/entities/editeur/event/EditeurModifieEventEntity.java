package fr.abes.licencesnationales.core.entities.editeur.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("modifie")
@NoArgsConstructor
@Getter @Setter
public class EditeurModifieEventEntity extends EditeurEventEntity {
    public EditeurModifieEventEntity(Object source, Integer id) {
        super(source);
        this.id = id;
    }

}
