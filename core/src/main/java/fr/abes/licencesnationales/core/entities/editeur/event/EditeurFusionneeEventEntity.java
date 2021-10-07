package fr.abes.licencesnationales.core.entities.editeur.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.List;

@Entity
@NoArgsConstructor
@DiscriminatorValue("fusionne")
@Getter @Setter
public class EditeurFusionneeEventEntity extends EditeurEventEntity {

    @Lob
    @Column(name = "ID_EDITEUR_FUSIONNES")
    protected List<Integer> idEditeurFusionnes;

}
