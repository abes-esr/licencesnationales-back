package fr.abes.licencesnationales.dto.editeur;

import lombok.Getter;

import java.util.List;

@Getter
public class EditeurFusionneDTO {
    private EditeurDTO editeurDTO;
    private List<Long> idEditeurFusionnes;
}
