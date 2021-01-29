package fr.abes.lnevent.event.editeur.dto.etablissement;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementDiviseDTO {
    private String ancienSiren;
    private ArrayList<Etablissement> etablissements;
}
