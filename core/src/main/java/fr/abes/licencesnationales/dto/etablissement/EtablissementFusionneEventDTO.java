package fr.abes.licencesnationales.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementFusionneEventDTO {
    private EtablissementEventDTO etablissementEventDTO;
    private ArrayList<String> sirenFusionnes;
}
