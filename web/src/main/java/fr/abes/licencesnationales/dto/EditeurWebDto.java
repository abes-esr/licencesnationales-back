package fr.abes.licencesnationales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class EditeurWebDto {
    @JsonProperty("nomEditeur")
    private String nomEditeur;

    @JsonProperty("identifiantEditeur")
    private String identifiantEditeur;

    @JsonProperty("groupesEtabRelies")
    private List<String> groupesEtabRelies;

    @JsonProperty("adresseEditeur")
    private String adresseEditeur;

}
