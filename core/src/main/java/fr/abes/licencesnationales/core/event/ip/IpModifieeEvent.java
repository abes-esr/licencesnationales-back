package fr.abes.licencesnationales.core.event.ip;

import fr.abes.licencesnationales.core.dto.ip.IpModifieeDto;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class IpModifieeEvent extends Event {
    private String siren;
    private Long id;
    private String ip;
    private boolean validee;
    private String typeAcces;
    private IpType typeIp;
    private String commentaires;

    public IpModifieeEvent(Object source, Long id, String siren, String ip, boolean validee, String typeAcces, IpType typeIp, String commentaires) {
        super(source);
        this.id = id;
        this.siren = siren;
        this.ip = ip;
        this.validee=validee;
        this.typeAcces=typeAcces;
        this.typeIp=typeIp;
        this.commentaires=commentaires;
    }
}
