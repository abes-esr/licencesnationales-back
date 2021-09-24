package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseWebDto {
    @JsonProperty("sirenScinde")
    private String sirenScinde;
    @JsonProperty(value = "nouveauxEtab")
    private ArrayList<EtablissementCreeSansCaptchaWebDto> nouveauxEtabs;
}
