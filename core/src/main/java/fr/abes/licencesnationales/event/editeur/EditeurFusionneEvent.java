package fr.abes.licencesnationales.event.editeur;


import fr.abes.licencesnationales.dto.editeur.EditeurDTO;
import fr.abes.licencesnationales.event.Event;
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
