package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.event.Event;
import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
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
