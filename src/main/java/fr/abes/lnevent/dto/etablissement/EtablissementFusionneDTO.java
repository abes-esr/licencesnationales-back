package fr.abes.lnevent.dto.etablissement;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementFusionneDTO {
    private Etablissement etablissement;
    private ArrayList<String> sirenFusionnes;
}
