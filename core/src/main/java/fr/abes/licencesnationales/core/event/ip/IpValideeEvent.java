package fr.abes.licencesnationales.core.event.ip;

import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class IpValideeEvent extends Event {

    private Long id;
    private String ip;
    private String siren;

    public IpValideeEvent(Object source, String ip, String siren) {
        super(source);
        this.ip = ip;
        this.siren = siren;
    }
}
