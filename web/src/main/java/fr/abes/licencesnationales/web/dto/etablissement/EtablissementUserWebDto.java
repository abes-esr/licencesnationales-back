package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.licencesnationales.web.dto.ip.IpWebDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EtablissementUserWebDto extends EtablissementWebDto {
    @JsonProperty("ips")
    private List<IpWebDto> ips = new ArrayList<>();

    public void ajouterIp(IpWebDto ip) {
        ips.add(ip);
    }
}
