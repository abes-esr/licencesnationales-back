package fr.abes.lnevent.collection;

import fr.abes.lnevent.event.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Event")
@NoArgsConstructor
public class EventCollection {

    public EventCollection(EtablissementCreeEvent etablissementCreeEvent) {
        this.event = "cree";
        this.nomEtab = etablissementCreeEvent.getNom();
    }

    public EventCollection(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.nomEtab = etablissementSupprimeEvent.getNom();
    }

    public EventCollection(EtablissementDiviseEvent etablissementDiviseEvent) {
        this.event = "divise";
        this.ancienNomEtab = etablissementDiviseEvent.getVieuxNom();
        this.etablisementsDivise = etablissementDiviseEvent.getEtablisementsDivise();
    }

    public EventCollection(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.nomEtab = etablissementFusionneEvent.getNom();
        this.etablissementsFusionne = etablissementFusionneEvent.getEtablissementsFusionne();
    }


    private EventCollection(String id, String event, String nomEtab, String ancienNomEtab, List<String> etablisementsDivise, List<String> etablissementsFusionne) {
        this.id = id;
        this.event = event;
        this.nomEtab = nomEtab;
        this.ancienNomEtab = ancienNomEtab;
        this.etablisementsDivise = etablisementsDivise;
        this.etablissementsFusionne = etablissementsFusionne;
    }

    @Id
    public String id;

    public String event;

    public String nomEtab;

    public String ancienNomEtab;

    public List<String> etablisementsDivise;

    public List<String> etablissementsFusionne;


}
