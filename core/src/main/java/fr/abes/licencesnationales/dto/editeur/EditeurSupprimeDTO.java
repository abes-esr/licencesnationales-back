package fr.abes.licencesnationales.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurSupprimeDTO {

    @NotBlank
    private String id;
   /* @NotBlank
    private String siren;*/
}
