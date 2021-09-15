package fr.abes.licencesnationales.core.event.ip;

import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class IpSupprimeeEvent extends Event {
    private Integer id;
    private String siren;

    public IpSupprimeeEvent(Object source, Integer id, String siren) {
        super(source);
        this.id = id;
        this.siren = siren;
    }
}
