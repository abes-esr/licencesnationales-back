package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactWebDto {
    @JsonProperty("nom")
    private String nom;
    @JsonProperty("prenom")
    private String prenom;
    @JsonProperty("mail")
    private String mail;
    @JsonProperty("telephone")
    private String telephone;
    @JsonProperty("adresse")
    private String adresse;
    @JsonProperty("boitePostale")
    private String boitePostale;
    @JsonProperty("codePostal")
    private String codePostal;
    @JsonProperty("cedex")
    private String cedex;
    @JsonProperty("ville")
    private String ville;
    @JsonProperty("role")
    private String role;

}
