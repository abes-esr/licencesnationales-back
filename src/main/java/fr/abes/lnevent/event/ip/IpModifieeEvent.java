package fr.abes.lnevent.event.ip;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class IpModifieeEvent extends Event {
    private Long id;
    private String ip;
    private String siren;

    public IpModifieeEvent(Object source, Long id, String ip, String siren) {
        super(source);
        this.id = id;
        this.ip = ip;
        this.siren = siren;
    }
}
