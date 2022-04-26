package fr.abes.licencesnationales.core.entities.ip.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

@Entity
@NoArgsConstructor
@DiscriminatorValue("supprime")
@Getter
@Setter
public class IpSupprimeeEventEntity extends IpEventEntity {

    @Column(length = 4000)
    @Size(max = 4000)
    private String commentaire;

    public IpSupprimeeEventEntity(Object source) {
        super(source);
    }

    public IpSupprimeeEventEntity(Object source, Integer ipId) {
        super(source, ipId);
    }

    public IpSupprimeeEventEntity(Object source, Integer ipId, String ip) {
        super(source, ipId, ip);
    }

    public IpSupprimeeEventEntity(Object source, Integer id, String ip, String siren) {
        super(source, id, ip, siren);
    }
}
