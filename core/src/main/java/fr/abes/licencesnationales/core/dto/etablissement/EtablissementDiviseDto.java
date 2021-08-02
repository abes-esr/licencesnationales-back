package fr.abes.licencesnationales.core.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementDiviseDto {
    private String ancienSiren;
    private ArrayList<EtablissementDto> etablissementDtos;
}
