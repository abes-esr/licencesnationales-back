package fr.abes.lnevent.event.editeur.dto.editeur;

import lombok.Getter;

import java.util.List;

@Getter
public class EditeurFusionneDTO {
    private Editeur editeur;
    private List<String> idEditeurFusionnes;
}
