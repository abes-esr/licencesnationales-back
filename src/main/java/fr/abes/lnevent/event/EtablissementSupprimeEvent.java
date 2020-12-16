package fr.abes.lnevent.event;

import lombok.Getter;
import lombok.Setter;

@Getter
public class EtablissementSupprimeEvent extends Event {
    private String nom;

    public EtablissementSupprimeEvent(Object source, String nom) {
        super(source);
        this.nom = nom;
    }
}
