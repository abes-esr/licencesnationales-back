package fr.abes.licencesnationales.core.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class IpDto {
    private String siren;
    private String id;
    private String ip;
    private String validee;
    private String dateCreation;
    private String dateModification;
    private String typeAcces;
    private String typeIp;
}
