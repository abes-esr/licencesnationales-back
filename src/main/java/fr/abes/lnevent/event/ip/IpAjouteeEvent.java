package fr.abes.lnevent.event.ip;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

import java.util.Date;

@Getter
public class IpAjouteeEvent extends Event {

    private String ip;

    private String siren;

    private Date dateCreation;

    private Date dateModification;

    private String typeAcces;

    private String typeIp;



    public IpAjouteeEvent(Object source, String ip, String siren, String typeAcces, String typeIp) {
        super(source);
        this.ip = ip;
        this.siren = siren;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.typeAcces = typeAcces;
        this.typeIp = typeIp;

    }
}
