package fr.abes.licencesnationales.core.entities.ip.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@DiscriminatorValue("supprime")
@Getter
@Setter
public class IpSupprimeeEventEntity extends IpEventEntity {
    public IpSupprimeeEventEntity(Object source) {
        super(source);
    }

    public IpSupprimeeEventEntity(Object source, Integer ipId) {
        super(source, ipId);
    }

    public IpSupprimeeEventEntity(Object source, Integer ipId, String ip) {
        super(source, ipId, ip);
    }

}
