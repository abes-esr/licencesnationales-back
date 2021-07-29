package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementEventDTO;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private EtablissementEventDTO etablissementEventDTO;
    private ArrayList<String> sirenFusionne;
    
    public EtablissementFusionneEvent(Object source, EtablissementEventDTO etablissementEventDTO, ArrayList<String> sirenFusionne) {
        super(source);
        this.etablissementEventDTO = etablissementEventDTO;
        this.sirenFusionne = sirenFusionne;
    }
}
