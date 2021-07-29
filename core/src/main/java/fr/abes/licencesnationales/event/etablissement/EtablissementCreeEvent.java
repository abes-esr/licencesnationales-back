package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementEventDTO;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EtablissementCreeEvent extends Event {

    public EtablissementCreeEvent(
            Object source,
            EtablissementEventDTO etablissement) {
        super(source);
        this.etablissement = etablissement;
    }

    private EtablissementEventDTO etablissement;
}
