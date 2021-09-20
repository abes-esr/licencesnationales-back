package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementModifieWebDto {
    @JsonIgnore
    private String siren;
    @JsonProperty("contact")
    private ContactModifieWebDto contact;
}
