package fr.abes.licencesnationales.web.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PlageIpv4AjouteeWebDto extends IpAjouteeWebDto {
    public PlageIpv4AjouteeWebDto(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
    }
}
