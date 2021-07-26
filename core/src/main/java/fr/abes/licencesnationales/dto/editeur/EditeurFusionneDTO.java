package fr.abes.licencesnationales.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurFusionneDTO {
    private EditeurDTO editeurDTO;
    private List<Long> idEditeurFusionnes;
}
