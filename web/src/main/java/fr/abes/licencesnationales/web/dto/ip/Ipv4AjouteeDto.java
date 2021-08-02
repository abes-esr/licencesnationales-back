package fr.abes.licencesnationales.web.dto.ip;

import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ipv4AjouteeDto extends IpAjouteeDto {
    private String ip;

    public Ipv4AjouteeDto(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
        this.ip = ip;
    }
}
