package fr.abes.lnevent.event;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String vieuxNom;
    private ArrayList<String> etablisementsDivise;

    public EtablissementDiviseEvent(Object source, String vieuxNom, ArrayList<String> etablisementsDivise) {
        super(source);
        this.vieuxNom = vieuxNom;
        this.etablisementsDivise = etablisementsDivise;
    }
}
