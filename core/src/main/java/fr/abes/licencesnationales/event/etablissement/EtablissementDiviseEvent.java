package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String ancienSiren;
    private ArrayList<EtablissementDto> etablissementDtos;

    public EtablissementDiviseEvent(Object source, String ancienSiren, ArrayList<EtablissementDto> etablissementEventDTOS) {
        super(source);
        this.ancienSiren = ancienSiren;
        this.etablissementDtos = etablissementEventDTOS;
    }
}
