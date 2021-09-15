package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ContactCreeWebDto {

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("prenom")
    @NotNull
    private String prenom;

    @JsonProperty("mail")
    @NotNull
    private String mail;

    @JsonProperty("motDePasse")
    @NotNull
    private String motDePasse;

    @JsonProperty("telephone")
    @NotNull
    private String telephone;

    @JsonProperty("adresse")
    @NotNull
    private String adresse;

    @JsonProperty("boitePostale")
    private String boitePostale;

    @JsonProperty("codePostal")
    @NotNull
    private String codePostal;

    @JsonProperty("cedex")
    private String cedex;

    @JsonProperty("ville")
    @NotNull
    private String ville;

    @JsonProperty("role")
    @NotNull
    private String role;
}
