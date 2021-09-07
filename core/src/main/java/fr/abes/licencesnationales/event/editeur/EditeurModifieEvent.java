package fr.abes.licencesnationales.event.editeur;


import fr.abes.licencesnationales.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EditeurModifieEvent extends Event {

    private Long id;

    private EditeurModifieDto editeur;

    public EditeurModifieEvent(Object source,
                            EditeurModifieDto editeur) {
        super(source);
        this.editeur = editeur;
    }

}
