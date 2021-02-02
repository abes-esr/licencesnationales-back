package fr.abes.lnevent.event.ip;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class IpSupprimeeEvent extends Event {
    private String ip;
    private String siren;

    public IpSupprimeeEvent(Object source, String ip, String siren) {
        super(source);
        this.ip = ip;
        this.siren = siren;
    }
}
