package fr.abes.licencesnationales.event.ip;


import fr.abes.licencesnationales.dto.ip.IpAjouteeDto;
import fr.abes.licencesnationales.event.Event;
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
    private String commentaires;

    public IpAjouteeEvent(Object source, String siren, String typeIp, String typeAcces, String ip, String commentaires) {
        super(source);
        this.ip = ip;
        this.siren = siren;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.typeAcces = typeAcces;
        this.typeIp = typeIp;
        this.commentaires=commentaires;
    }

    public IpAjouteeEvent(Object source, IpAjouteeDto ipAjouteeDto) {
        super(source);
        this.ip = ipAjouteeDto.getIp();
        this.siren = ipAjouteeDto.getSiren();
        this.typeAcces = ipAjouteeDto.getTypeAcces();
        this.typeIp = ipAjouteeDto.getTypeIp();
        this.commentaires = ipAjouteeDto.getCommentaires();
    }
}
