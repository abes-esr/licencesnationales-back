package fr.abes.licencesnationales.web.dto.editeur;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactEditeurWebDto {
    @JsonProperty("nom")
    public String nomContactTechnique;
    @JsonProperty("prenom")
    public String prenomContactTechnique;
    @JsonProperty("mail")
    public String mailContactTechnique;



}
