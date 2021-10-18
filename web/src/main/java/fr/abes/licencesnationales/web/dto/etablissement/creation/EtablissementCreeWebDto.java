package fr.abes.licencesnationales.web.dto.etablissement.creation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementCreeWebDto {

    @JsonProperty("nom")
    private String name;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("typeEtablissement")
    private String typeEtablissement;

    @JsonProperty("contact")
    private ContactCreeWebDto contact;

    @JsonProperty("recaptcha")
    private String recaptcha;
}
