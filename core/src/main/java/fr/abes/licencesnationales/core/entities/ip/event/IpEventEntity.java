package fr.abes.licencesnationales.core.entities.ip.event;

import fr.abes.licencesnationales.core.entities.EventEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "IpEvent")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event", columnDefinition = "varchar(20)", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
@Getter
@Setter
public class IpEventEntity extends EventEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ipevent_Sequence")
    @SequenceGenerator(name = "ipevent_Sequence", sequenceName = "IPEVENT_SEQ", allocationSize = 1)
    protected Integer id;

    @Column(name = "SIREN")
    protected String siren;

    @Column(name = "IP")
    protected String ip;

    public IpEventEntity(Object source) {
        super(source);
    }

    public IpEventEntity(Object source, Integer id) {
        super(source);
        this.id = id;
    }

    public IpEventEntity(Object source, String ip) {
        super(source);
        this.ip = ip;
    }

}
