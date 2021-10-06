package fr.abes.licencesnationales.web.dto.etablissement.scission;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeSansCaptchaWebDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseWebDto {
    @JsonProperty("sirenScinde")
    private String sirenScinde;
    @JsonProperty(value = "nouveauxEtabs")
    private ArrayList<EtablissementCreeSansCaptchaWebDto> nouveauxEtabs = new ArrayList<>();

    public void ajouterNouvelEtab(EtablissementCreeSansCaptchaWebDto etab) {
        this.nouveauxEtabs.add(etab);
    }
}
