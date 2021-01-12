package fr.abes.lnevent.event;

import lombok.Getter;

@Getter
public class IpAjouteeEvent extends Event {

    private String ip;

    private String siren;

    public IpAjouteeEvent(Object source, String ip, String siren) {
        super(source);
        this.ip = ip;
        this.siren = siren;
    }
}
