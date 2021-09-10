package fr.abes.licencesnationales.core.entities.ip;

import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.core.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.core.event.ip.IpValideeEvent;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "IpEvent")
@NoArgsConstructor
public class IpEventEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "ipevent_Sequence")
    @SequenceGenerator(name = "ipevent_Sequence", sequenceName = "IPEVENT_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "DATE_CREATION_EVENT")
    private Date dateCreationEvent;

    @Column(name = "EVENT")
    private String event;

    @Column(name = "SIREN")
    private String siren;

    @Column(name = "IP")
    private String ip;



    public IpEventEntity(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoutee";
        this.dateCreationEvent = ipAjouteeEvent.created;
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
    }

    public IpEventEntity(IpModifieeEvent ipModifieeEvent) {
        this.event = "ipModifiee";
        this.dateCreationEvent = ipModifieeEvent.created;
        this.ip = ipModifieeEvent.getIp();
        this.siren = ipModifieeEvent.getSiren();
    }

    public IpEventEntity(IpValideeEvent ipValideeEvent) {
        this.event = "ipValidee";
        this.dateCreationEvent = ipValideeEvent.created;
        this.ip = ipValideeEvent.getIp();
        this.siren = ipValideeEvent.getSiren();
    }

    public IpEventEntity(IpSupprimeeEvent ipSupprimeeEvent) {
        this.event = "ipSupprimee";
        this.dateCreationEvent = ipSupprimeeEvent.created;
        this.id = ipSupprimeeEvent.getId();
        this.siren = ipSupprimeeEvent.getSiren();
    }
}
