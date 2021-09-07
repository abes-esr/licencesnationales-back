package fr.abes.licencesnationales.event.ip;

import fr.abes.licencesnationales.dto.ip.IpModifieeDto;
import fr.abes.licencesnationales.event.Event;
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

    public IpModifieeEvent(Object source, IpModifieeDto ipModifieeDto) {
        super(source);
        this.siren = ipModifieeDto.getSiren();
        this.id = ipModifieeDto.getId();
        this.ip = ipModifieeDto.getIp();
        this.typeAcces = ipModifieeDto.getTypeAcces();
        this.typeIp = ipModifieeDto.getTypeIp();
    }
}
