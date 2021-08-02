package fr.abes.licencesnationales.core.event.editeur;


import fr.abes.licencesnationales.core.dto.editeur.EditeurFusionneDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;

import java.util.List;

@Getter
public class EditeurFusionneEvent extends Event {
    private List<Long> idEditeurFusionnes;
    private EditeurFusionneDto editeur;

    public EditeurFusionneEvent(Object source, EditeurFusionneDto editeur, List<Long> idEditeurFusionnes) {
        super(source);
        this.editeur = editeur;
        this.idEditeurFusionnes = idEditeurFusionnes;
    }


}
