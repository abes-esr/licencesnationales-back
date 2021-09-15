package fr.abes.licencesnationales.core.event.editeur;

import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class EditeurSupprimeEvent extends Event {
    private Integer id;

    public EditeurSupprimeEvent(Object source, Integer id) {
        super(source);
        this.id = id;
    }
}
