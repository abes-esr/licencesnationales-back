package fr.abes.licencesnationales.web.dto.editeur;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class EditeurWebDtoList {
    @JsonProperty("nom")
    private String nom;

    @JsonProperty("id")
    private Integer id;

    public Date dateCreation;

}
