package fr.abes.licencesnationales.core.event.ip;


import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class IpAjouteeEvent extends Event {
    private String ip;
    private String siren;
    private Date dateCreation;
    private Date dateModification;
    private String typeAcces;
    private IpType typeIp;
    private String commentaires;

    public IpAjouteeEvent(Object source) {
        super(source);
    }

    public IpAjouteeEvent(Object source, String siren, IpType typeIp, String typeAcces, String ip, String commentaires) {
        super(source);
        this.ip = ip;
        this.siren = siren;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.typeAcces = typeAcces;
        this.typeIp = typeIp;
        this.commentaires=commentaires;
    }

}
