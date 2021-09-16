package fr.abes.licencesnationales.core.entities.editeur.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("fusionne")
@Getter @Setter
public class EditeurFusionneeEventEntity extends EditeurEventEntity {

}
