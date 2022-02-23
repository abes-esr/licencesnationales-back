package fr.abes.licencesnationales.core.entities.ip.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("rejete")
@Getter
@Setter
public class IpRejeteeEventEntity extends IpEventEntity {
    public IpRejeteeEventEntity(Object source) {
        super(source);
    }

    public IpRejeteeEventEntity(Object source, Integer ipId) {
        super(source, ipId);
    }

    public IpRejeteeEventEntity(Object source, Integer id, String ip, String siren) {
        super(source, id, ip, siren);
    }
}
