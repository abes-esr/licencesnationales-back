package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.event.Event;
import fr.abes.lnevent.event.editeur.dto.etablissement.Etablissement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private Etablissement etablissement;
    private ArrayList<String> sirenFusionne;
    
    public EtablissementFusionneEvent(Object source, Etablissement etablissement, ArrayList<String> sirenFusionne) {
        super(source);
        this.etablissement = etablissement;
        this.sirenFusionne = sirenFusionne;
    }
}
