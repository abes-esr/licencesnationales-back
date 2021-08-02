package fr.abes.licencesnationales.core.event.editeur;


import fr.abes.licencesnationales.core.dto.editeur.EditeurModifieDto;
import fr.abes.licencesnationales.core.event.Event;
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
