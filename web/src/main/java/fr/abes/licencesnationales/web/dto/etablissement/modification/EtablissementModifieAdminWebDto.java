package fr.abes.licencesnationales.web.dto.etablissement.modification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementModifieAdminWebDto extends EtablissementModifieWebDto {
    @JsonProperty("nom")
    private String nom;
    @JsonProperty("typeEtablissement")
    private String typeEtablissement;
}
