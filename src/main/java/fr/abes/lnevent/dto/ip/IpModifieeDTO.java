package fr.abes.lnevent.dto.ip;

import lombok.Getter;

@Getter
public class IpModifieeDTO {
    //private String siren; pas besoin on va le chercher dans le security contextuser
    private Long id;
    private String ip;
    private String validee;
    //private String dateModification; pas besoin on fixe la date dans le listener
    private String typeAcces;
    private String typeIp;
    private String commentaires;

    public IpModifieeDTO() {
    }
}
