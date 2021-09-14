package fr.abes.licencesnationales.web.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpAjouteeWebDto {
    private String siren;
    private String typeIp;
    private String ip;
    private String typeAcces;
    private String commentaires;
}
