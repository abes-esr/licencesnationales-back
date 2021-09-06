package fr.abes.licencesnationales.core.event.editeur;

import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class EditeurSupprimeEvent extends Event {
    private Long id;

    public EditeurSupprimeEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }
}
