package fr.abes.licencesnationales.dto.editeur;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class EditeurSupprimeDTO {

    @NotBlank
    private String id;
   /* @NotBlank
    private String siren;*/
}
