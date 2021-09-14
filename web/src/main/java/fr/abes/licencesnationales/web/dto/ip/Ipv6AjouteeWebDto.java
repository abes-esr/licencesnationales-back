package fr.abes.licencesnationales.web.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class Ipv6AjouteeWebDto extends IpAjouteeWebDto {
    public Ipv6AjouteeWebDto(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
    }
}
