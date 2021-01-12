package fr.abes.lnevent.event;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String ancienSiren;
    private ArrayList<String> sirens;

    public EtablissementDiviseEvent(Object source, String ancienSiren, ArrayList<String> sirens) {
        super(source);
        this.ancienSiren = ancienSiren;
        this.sirens = sirens;
    }
}
