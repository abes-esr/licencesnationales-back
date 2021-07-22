package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementDTO;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private EtablissementDTO etablissementDTO;
    private ArrayList<String> sirenFusionne;
    
    public EtablissementFusionneEvent(Object source, EtablissementDTO etablissementDTO, ArrayList<String> sirenFusionne) {
        super(source);
        this.etablissementDTO = etablissementDTO;
        this.sirenFusionne = sirenFusionne;
    }
}
