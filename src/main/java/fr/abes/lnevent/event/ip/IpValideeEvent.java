package fr.abes.lnevent.event.ip;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class IpValideeEvent extends Event {

    private String id;
    private String ip;
    private String siren;

    public IpValideeEvent(Object source, String ip, String siren) {
        super(source);
        this.ip = ip;
        this.siren = siren;
    }
}
