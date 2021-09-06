package fr.abes.licencesnationales.core.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IpValideeDto {
    private String ip;
    private String siren;
}
