package fr.abes.licencesnationales.core.event.etablissement;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String ancienSiren;
    private List<EtablissementEntity> etablissements;

    public EtablissementDiviseEvent(Object source) {
        super(source);
        etablissements = new ArrayList<>();
    }

    public EtablissementDiviseEvent(Object source, String ancienSiren, List<EtablissementEntity> etablissementEvents) {
        super(source);
        this.ancienSiren = ancienSiren;
        this.etablissements = etablissementEvents;
    }
}
