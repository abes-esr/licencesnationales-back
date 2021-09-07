package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EtablissementSupprimeEvent extends Event {
    private String siren;

    public EtablissementSupprimeEvent(Object source, String siren) {
        super(source);
        this.siren = siren;
    }
}
