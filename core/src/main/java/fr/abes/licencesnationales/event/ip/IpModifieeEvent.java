package fr.abes.licencesnationales.event.ip;

import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class IpModifieeEvent extends Event {
    private String siren;
    private Long id;
    private String ip;
    private String validee;
    private String typeAcces;
    private String typeIp;
    private String commentaires;

    public IpModifieeEvent(Object source, String siren, Long id, String ip, String validee, String typeAcces, String typeIp, String commentaires) {
        super(source);
        this.siren = siren;
        this.id = id;
        this.ip = ip;
        this.validee=validee;
        this.typeAcces=typeAcces;
        this.typeIp=typeIp;
        this.commentaires=commentaires;
    }
}
