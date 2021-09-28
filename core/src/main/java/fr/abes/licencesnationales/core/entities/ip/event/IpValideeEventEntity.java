package fr.abes.licencesnationales.core.entities.ip.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("valide")
@Getter
@Setter
public class IpValideeEventEntity extends IpEventEntity {
    public IpValideeEventEntity(Object source) {
        super(source);
    }

    public IpValideeEventEntity(Object source, Integer id) {
        super(source, id);
    }
}
