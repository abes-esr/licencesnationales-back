package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpAjouteeDto {
    private String siren;
    private String typeIp;
    private String ip;
    private String typeAcces;
    private String commentaires;


}
