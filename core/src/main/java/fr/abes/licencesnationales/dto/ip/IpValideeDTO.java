package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IpValideeDTO {
    private String ip;
    private String siren;
}
