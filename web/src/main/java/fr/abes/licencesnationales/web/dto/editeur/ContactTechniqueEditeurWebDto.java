package fr.abes.licencesnationales.web.dto.editeur;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactTechniqueEditeurWebDto {
    @JsonProperty("nomContactTechnique")
    public String nomContactTechnique;
    @JsonProperty("prenomContactTechnique")
    public String prenomContactTechnique;
    @JsonProperty("mailContactTechnique")
    public String mailContactTechnique;



}
