package fr.abes.licencesnationales.core.event.ip;

import fr.abes.licencesnationales.core.dto.ip.IpModifieeDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class IpModifieeEvent extends Event {
    private String siren;
    private Long id;
    private String ip;
    private boolean validee;
    private String typeAcces;
    private String typeIp;
    private String commentaires;

    public IpModifieeEvent(Object source, String siren, String ip, boolean validee, String typeAcces, String typeIp, String commentaires) {
        super(source);
        this.siren = siren;
        this.ip = ip;
        this.validee=validee;
        this.typeAcces=typeAcces;
        this.typeIp=typeIp;
        this.commentaires=commentaires;
    }

}
