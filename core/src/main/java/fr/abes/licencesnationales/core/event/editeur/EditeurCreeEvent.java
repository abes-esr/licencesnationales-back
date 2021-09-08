package fr.abes.licencesnationales.core.event.editeur;


import fr.abes.licencesnationales.core.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

@Getter
public class EditeurCreeEvent extends Event {
    private EditeurCreeDto editeur;

    public EditeurCreeEvent(Object source, EditeurCreeDto editeur) {
        super(source);
        this.editeur = editeur;
    }
}
