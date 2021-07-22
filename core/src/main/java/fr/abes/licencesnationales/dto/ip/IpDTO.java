package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class IpDTO {
    private String siren;
    private String id;
    private String ip;
    private String validee;
    private String dateCreation;
    private String dateModification;
    private String typeAcces;
    private String typeIp;
}
