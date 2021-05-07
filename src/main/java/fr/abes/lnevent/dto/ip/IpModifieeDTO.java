package fr.abes.lnevent.dto.ip;

import lombok.Getter;

@Getter
public class IpModifieeDTO {
    private String siren;
    private Long id;
    private String ip;
    private String validee;
    private String dateModification;
    private String typeAcces;
    private String typeIp;
}
