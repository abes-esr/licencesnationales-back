package fr.abes.lnevent.event;

import fr.abes.lnevent.dto.Etablissement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseEvent extends Event {
    private String ancienSiren;
    private ArrayList<Etablissement> etablissements;

    public EtablissementDiviseEvent(Object source, String ancienSiren, ArrayList<Etablissement> etablissements) {
        super(source);
        this.ancienSiren = ancienSiren;
        this.etablissements = etablissements;
    }
}
