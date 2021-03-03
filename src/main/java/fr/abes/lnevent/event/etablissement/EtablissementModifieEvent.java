package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class EtablissementModifieEvent extends Event {
    public EtablissementModifieEvent(
            Object source,
            Long idEtablissement,
            EtablissementDTO etablissement) {
        super(source);
        this.idEtablissement = idEtablissement;
        this.etablissement = etablissement;
    }


    private Long idEtablissement;

    private EtablissementDTO etablissement;
}
