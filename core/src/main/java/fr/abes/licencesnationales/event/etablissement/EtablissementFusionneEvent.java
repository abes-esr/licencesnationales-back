package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private EtablissementDto etablissementDto;
    private ArrayList<String> sirenFusionne;
    
    public EtablissementFusionneEvent(Object source, EtablissementDto etablissementDto, ArrayList<String> sirenFusionne) {
        super(source);
        this.etablissementDto = etablissementDto;
        this.sirenFusionne = sirenFusionne;
    }
}
