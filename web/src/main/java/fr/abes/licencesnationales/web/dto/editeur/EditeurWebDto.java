package fr.abes.licencesnationales.web.dto.editeur;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class EditeurWebDto {

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("identifiantBis")
    private String identifiant;

    @JsonProperty("typesEtablissements")
    private List<String> typesEtablissements = new ArrayList<>();

    @JsonProperty("adresse")
    private String adresse;

    private String dateCreation;

    @JsonProperty("contactsCommerciaux")
    private List<ContactEditeurWebDto> contactsCommerciaux = new ArrayList<>();

    @JsonProperty("contactsTechniques")
    private List<ContactEditeurWebDto> contactsTechniques = new ArrayList<>();


    public void ajouterTypeEtablissement(String type) {
        this.typesEtablissements.add(type);
    }

    public void ajouterContactCommercial(ContactEditeurWebDto dto) {
        this.contactsCommerciaux.add(dto);
    }

    public void ajouterContactTechnique(ContactEditeurWebDto dto) {
        this.contactsTechniques.add(dto);
    }
}
