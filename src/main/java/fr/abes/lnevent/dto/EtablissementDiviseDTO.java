package fr.abes.lnevent.dto;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementDiviseDTO {
    private String ancienSiren;
    private ArrayList<Etablissement> etablissements;
}
