package fr.abes.licencesnationales.web.dto.editeur;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurModifieWebDto {
    @JsonProperty
    private Integer id;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("identifiantBis")
    private String identifiantBis;

    @JsonProperty("typesEtablissements")
    private List<String> typesEtablissements;

    @JsonProperty("adresse")
    private String adresse;

    @JsonProperty("contactsCommerciaux")
    public Set<ContactEditeurWebDto> contactsCommerciaux;

    @JsonProperty("contactsTechniques")
    public Set<ContactEditeurWebDto> contactsTechniques;


}
