package fr.abes.licencesnationales.web.dto.ip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Ipv4AjouteeWebDto extends IpAjouteeWebDto {

    public Ipv4AjouteeWebDto(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
    }
}
