package fr.abes.lnevent.event.editeur;

import fr.abes.lnevent.event.Event;
import fr.abes.lnevent.dto.editeur.EditeurDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class EditeurFusionneEvent extends Event {

    public EditeurFusionneEvent(Object source, EditeurDTO editeurDTO, List<Long> idEditeurFusionnes) {
        super(source);
        this.editeurDTO = editeurDTO;
        this.idEditeurFusionnes = idEditeurFusionnes;
    }

    private EditeurDTO editeurDTO;
    private List<Long> idEditeurFusionnes;
}
