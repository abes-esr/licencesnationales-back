package fr.abes.licencesnationales.core.entities.editeur.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("cree")
@Getter @Setter
public class EditeurCreeEventEntity extends EditeurEventEntity {

    /**
     * ce constructeur est déprécié parce qu'il est requis par JPA mais il ne devrait jamais être utilisé ailleurs que par JPA
     * à ne pas utiliser
     */
    @Deprecated
    public EditeurCreeEventEntity() {
        super();
    }

    public EditeurCreeEventEntity(Object source) {
        super(source);
    }

}
