package fr.abes.licencesnationales.event.editeur;


import fr.abes.licencesnationales.dto.editeur.EditeurCreeDto;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EditeurCreeEvent extends Event {
    private EditeurCreeDto editeur;

    public EditeurCreeEvent(Object source, EditeurCreeDto editeur) {
        super(source);
        this.editeur = editeur;
    }
}
