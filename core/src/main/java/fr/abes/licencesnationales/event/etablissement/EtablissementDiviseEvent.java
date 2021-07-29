package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementEventDTO;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String ancienSiren;
    private ArrayList<EtablissementEventDTO> etablissementEventDTOS;

    public EtablissementDiviseEvent(Object source, String ancienSiren, ArrayList<EtablissementEventDTO> etablissementEventDTOS) {
        super(source);
        this.ancienSiren = ancienSiren;
        this.etablissementEventDTOS = etablissementEventDTOS;
    }
}
