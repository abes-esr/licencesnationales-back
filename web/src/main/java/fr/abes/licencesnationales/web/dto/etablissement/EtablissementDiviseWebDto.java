package fr.abes.licencesnationales.web.dto.etablissement;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementDiviseWebDto {
    private String ancienSiren;
    private ArrayList<EtablissementWebDto> etablissementDtos;
}
