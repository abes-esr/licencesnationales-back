package fr.abes.licencesnationales.core.event.ip;


import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

import java.util.Date;

@Getter
public class IpAjouteeEvent extends Event {
    private String ip;
    private String siren;
    private String typeAcces;
    private String typeIp;
    private String commentaires;

    public IpAjouteeEvent(Object source, String siren, String typeIp, String typeAcces, String ip, String commentaires) {
        super(source);
        this.ip = ip;
        this.siren = siren;
        this.typeAcces = typeAcces;
        this.typeIp = typeIp;
        this.commentaires=commentaires;
    }

}
