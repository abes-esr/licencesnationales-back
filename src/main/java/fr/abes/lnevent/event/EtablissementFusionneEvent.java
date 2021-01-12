package fr.abes.lnevent.event;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private String siren;
    private ArrayList<String> sirenFusionne;
    
    public EtablissementFusionneEvent(Object source, String siren, ArrayList<String> sirenFusionne) {
        super(source);
        this.siren = siren;
        this.sirenFusionne = sirenFusionne;
    }
}
