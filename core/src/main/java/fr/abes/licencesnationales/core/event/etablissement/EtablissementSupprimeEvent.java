package fr.abes.licencesnationales.core.event.etablissement;


import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class EtablissementSupprimeEvent extends Event {
    private String siren;

    public EtablissementSupprimeEvent(Object source, String siren) {
        super(source);
        this.siren = siren;
    }
}
