package fr.abes.licencesnationales.event.ip;

import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class IpSupprimeeEvent extends Event {
    private Long id;
    private String siren;

    public IpSupprimeeEvent(Object source, Long id, String siren) {
        super(source);
        this.id = id;
        this.siren = siren;
    }
}
