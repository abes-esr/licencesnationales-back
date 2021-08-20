package fr.abes.licencesnationales.web.dto.editeur;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactCommercialEditeurWebDto {
    @JsonProperty("nomContactCommercial")
    public String nomContactCommercial;
    @JsonProperty("prenomContactCommercial")
    public String prenomContactCommercial;
    @JsonProperty("mailContactCommercial")
    public String mailContactCommercial;



}
