package fr.abes.licencesnationales.web.dto.etablissement;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementAdminWebDto extends EtablissementWebDto {
    @JsonProperty("dateModificationDerniereIp")
    private String dateModificationDerniereIp;
}
