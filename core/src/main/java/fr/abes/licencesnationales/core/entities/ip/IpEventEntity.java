package fr.abes.licencesnationales.core.entities.ip;

import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.core.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.core.event.ip.IpValideeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "IpEvent")
@NoArgsConstructor
@Getter
@Setter
public class IpEventEntity extends EventEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ipevent_Sequence")
    @SequenceGenerator(name = "ipevent_Sequence", sequenceName = "IPEVENT_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "SIREN")
    private String siren;

    @Column(name = "IP")
    private String ip;

    /**
     * CTOR à partir d'un événement d'ajout d'IP
     *
     * @param ipAjouteeEvent Evénement d'ajout d'IP
     */
    public IpEventEntity(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoutee";
        this.dateCreationEvent = ipAjouteeEvent.created;
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
    }

    /**
     * CTOR à partir d'un événement de modification d'IP
     *
     * @param ipModifieeEvent Evénement de modification d'IP
     */
    public IpEventEntity(IpModifieeEvent ipModifieeEvent) {
        this.event = "ipModifiee";
        this.dateCreationEvent = ipModifieeEvent.created;
        this.ip = ipModifieeEvent.getIp();
        this.siren = ipModifieeEvent.getSiren();
    }

    /**
     * CTOR à partir d'un événement de validation d'IP
     *
     * @param ipValideeEvent Evénement de validation d'IP
     */
    public IpEventEntity(IpValideeEvent ipValideeEvent) {
        this.event = "ipValidee";
        this.dateCreationEvent = ipValideeEvent.created;
        this.ip = ipValideeEvent.getIp();
        this.siren = ipValideeEvent.getSiren();
    }

    /**
     * CTOR à partir d'un événement de suppression d'IP
     *
     * @param ipSupprimeeEvent Evénement de suppression d'IP
     */
    public IpEventEntity(IpSupprimeeEvent ipSupprimeeEvent) {
        this.event = "ipSupprimee";
        this.dateCreationEvent = ipSupprimeeEvent.created;
        this.id = ipSupprimeeEvent.getId();
        this.siren = ipSupprimeeEvent.getSiren();
    }
}
