package fr.abes.licencesnationales.core.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementFusionneDto {
    private EtablissementDto etablissementDTO;
    private ArrayList<String> sirenFusionnes;
}
