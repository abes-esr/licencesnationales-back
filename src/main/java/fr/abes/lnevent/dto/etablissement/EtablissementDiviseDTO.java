package fr.abes.lnevent.dto.etablissement;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementDiviseDTO {
    private String ancienSiren;
    private ArrayList<EtablissementDTO> etablissementDTOS;
}
