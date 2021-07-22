package fr.abes.licencesnationales.event.editeur;

import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EditeurSupprimeEvent extends Event {
    private String id;
    //private String siren;

    public EditeurSupprimeEvent(Object source, String id/*, String siren*/) {
        super(source);
        this.id = id;
        //this.siren = siren;
    }
}
