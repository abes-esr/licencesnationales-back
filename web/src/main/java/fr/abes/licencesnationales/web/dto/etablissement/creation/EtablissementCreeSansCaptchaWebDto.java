package fr.abes.licencesnationales.web.dto.etablissement.creation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementCreeSansCaptchaWebDto {
    @JsonProperty(value = "nom")
    private String nom;
    @JsonProperty(value = "siren")
    private String siren;
    @JsonProperty(value = "typeEtablissement")
    private String typeEtablissement;
    @JsonProperty(value = "contact")
    private ContactCreeWebDto contact;


}
