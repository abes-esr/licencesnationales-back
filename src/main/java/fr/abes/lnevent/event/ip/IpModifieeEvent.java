package fr.abes.lnevent.event.ip;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class IpModifieeEvent extends Event {
    private String siren;
    private Long id;
    private String ip;
    private String validee;
    private String dateModification;
    private String typeAcces;
    private String typeIp;

    public IpModifieeEvent(Object source, String siren, Long id, String ip, String validee, String dateModification, String typeAcces, String typeIp) {
        super(source);
        this.siren = siren;
        this.id = id;
        this.ip = ip;
        this.validee=validee;
        this.dateModification=dateModification;
        this.typeAcces=typeAcces;
        this.typeIp=typeIp;
    }
}
