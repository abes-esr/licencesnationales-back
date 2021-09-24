package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EtablissementFusionneWebDto {
    @JsonProperty(value = "sirenFusionnes")
    private List<String> sirenFusionnes;
    @JsonProperty(value = "nouveauEtab")
    private EtablissementCreeSansCaptchaWebDto nouveauEtab;
}
