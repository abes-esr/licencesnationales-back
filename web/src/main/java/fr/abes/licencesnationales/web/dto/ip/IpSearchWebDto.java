package fr.abes.licencesnationales.web.dto.ip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IpSearchWebDto {
    private Integer id;
    private String nomEtab;
    private String siren;
    private String idAbes;
    private String ip;
}
