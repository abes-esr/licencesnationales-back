package fr.abes.lnevent.dto;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementFusionneDTO {
    private String siren;
    private ArrayList<String> sirenFusionne;
}
