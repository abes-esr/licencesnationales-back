package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class EtablissementCreeEvent extends Event {

    public EtablissementCreeEvent(
            Object source,
            EtablissementDTO etablissement) {
        super(source);
        this.etablissement = etablissement;
    }

    private EtablissementDTO etablissement;
}
