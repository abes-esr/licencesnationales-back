package fr.abes.licencesnationales.core.entities.editeur.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("supprime")
@NoArgsConstructor
@Getter @Setter
public class EditeurSupprimeEventEntity extends EditeurEventEntity {
    public EditeurSupprimeEventEntity(Object source, Integer id) {
        super(source);
        this.id = id;
    }
}
