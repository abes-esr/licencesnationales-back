package fr.abes.lnevent.event;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private String nom;
    private ArrayList<String> etablissementsFusionne;
    
    public EtablissementFusionneEvent(Object source, String nom, ArrayList<String> etablissementsFusionne) {
        super(source);
        this.nom = nom;
        this.etablissementsFusionne = etablissementsFusionne;
    }
}
