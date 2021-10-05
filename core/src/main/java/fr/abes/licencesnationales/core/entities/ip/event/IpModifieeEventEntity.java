package fr.abes.licencesnationales.core.entities.ip.event;

import fr.abes.licencesnationales.core.entities.ip.IpType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@NoArgsConstructor
@DiscriminatorValue("modifie")
@Getter
@Setter
public class IpModifieeEventEntity extends IpEventEntity {
    private String typeAcces;
    private IpType typeIp;
    private String commentaires;
    private transient String statut;

    public IpModifieeEventEntity(Object source) {
        super(source);
    }

    public IpModifieeEventEntity(Object source, String ip, String commentaires) {
        super(source, ip);
        this.commentaires = commentaires;
    }
}
