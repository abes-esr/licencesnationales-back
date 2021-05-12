package fr.abes.lnevent.event.ip;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class IpSupprimeeEvent extends Event {
    private String id;
    private String siren;

    public IpSupprimeeEvent(Object source, String id, String siren) {
        super(source);
        this.id = id;
        this.siren = siren;
    }
}
