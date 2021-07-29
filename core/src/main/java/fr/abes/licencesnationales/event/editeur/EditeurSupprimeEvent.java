package fr.abes.licencesnationales.event.editeur;

import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EditeurSupprimeEvent extends Event {
    private String id;

    public EditeurSupprimeEvent(Object source, String id) {
        super(source);
        this.id = id;
    }
}
