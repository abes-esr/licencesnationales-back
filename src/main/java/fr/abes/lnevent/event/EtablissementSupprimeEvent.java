package fr.abes.lnevent.event;

import lombok.Getter;
import lombok.Setter;

@Getter
public class EtablissementSupprimeEvent extends Event {
    private String siren;

    public EtablissementSupprimeEvent(Object source, String siren) {
        super(source);
        this.siren = siren;
    }
}
