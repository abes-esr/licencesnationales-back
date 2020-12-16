package fr.abes.lnevent.event;

import lombok.Getter;

@Getter
public class EtablissementCreeEvent extends Event {
    private String nom;

    public EtablissementCreeEvent(Object source, String nom) {
        super(source);
        this.nom = nom;
    }
}
