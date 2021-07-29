package fr.abes.licencesnationales.dto.editeur;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurFusionneWebDto {
    @JsonProperty("editeur")
    private EditeurWebDto editeurWebDTO;
    @JsonProperty("idEditeurFusionne")
    private List<Long> idEditeurFusionnes;
}
