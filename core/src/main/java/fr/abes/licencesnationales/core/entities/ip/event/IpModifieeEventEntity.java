package fr.abes.licencesnationales.core.entities.ip.event;

import fr.abes.licencesnationales.core.entities.ip.IpType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("modifie")
@Getter
@Setter
public class IpModifieeEventEntity extends IpEventEntity {
    private String typeAcces;
    private IpType typeIp;
    private String commentaires;

    public IpModifieeEventEntity(Object source) {
        super(source);
    }

}
