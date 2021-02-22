package fr.abes.lnevent.dto.editeur;

import lombok.Getter;

import java.util.List;

@Getter
public class EditeurFusionneDTO {
    private EditeurDTO editeurDTO;
    private List<Long> idEditeurFusionnes;
}
