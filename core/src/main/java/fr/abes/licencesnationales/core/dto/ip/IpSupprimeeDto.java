package fr.abes.licencesnationales.core.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpSupprimeeDto {
    private Long id;
    private String siren;
}
