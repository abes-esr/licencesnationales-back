package fr.abes.licencesnationales.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementCreeDto {
    private EtablissementDto etablissementDTO;
    private String recaptcha;
}
