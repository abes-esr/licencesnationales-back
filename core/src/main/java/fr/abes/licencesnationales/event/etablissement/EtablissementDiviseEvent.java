package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementDTO;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String ancienSiren;
    private ArrayList<EtablissementDTO> etablissementDTOS;

    public EtablissementDiviseEvent(Object source, String ancienSiren, ArrayList<EtablissementDTO> etablissementDTOS) {
        super(source);
        this.ancienSiren = ancienSiren;
        this.etablissementDTOS = etablissementDTOS;
    }
}
