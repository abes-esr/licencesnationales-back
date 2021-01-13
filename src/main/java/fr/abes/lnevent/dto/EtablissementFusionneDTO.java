package fr.abes.lnevent.dto;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementFusionneDTO {
    private Etablissement etablissement;
    private ArrayList<String> sirenFusionne;
}
