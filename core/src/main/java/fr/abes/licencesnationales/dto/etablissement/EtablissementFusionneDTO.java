package fr.abes.licencesnationales.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementFusionneDTO {
    private EtablissementDTO etablissementDTO;
    private ArrayList<String> sirenFusionnes;
}
