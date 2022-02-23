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
@DiscriminatorValue("cree")
@Getter @Setter
public class IpCreeEventEntity extends IpEventEntity {
    private String commentaires;

    public IpCreeEventEntity(Object source) {
        super(source);
    }

    public IpCreeEventEntity(Object source, String ip, String commentaires) {
        super(source, ip);
        this.commentaires = commentaires;
    }

}
