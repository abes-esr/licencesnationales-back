package fr.abes.licencesnationales.core.entities.editeur.event;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("cree")
@NoArgsConstructor
@Getter @Setter
public class EditeurCreeEventEntity extends EditeurEventEntity {

    public EditeurCreeEventEntity(Object source) {
        super(source);
    }

}
