package fr.abes.licencesnationales.web.dto.etablissement.scission;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.licencesnationales.web.dto.etablissement.creation.EtablissementCreeSansCaptchaWebDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Getter
@Setter
public class EtablissementDiviseWebDto {
    @JsonProperty("sirenScinde")
    @NotNull
    private String sirenScinde;
    @JsonProperty(value = "nouveauxEtabs")
    @NotNull
    private ArrayList<EtablissementCreeSansCaptchaWebDto> nouveauxEtabs = new ArrayList<>();

    public void ajouterNouvelEtab(EtablissementCreeSansCaptchaWebDto etab) {
        this.nouveauxEtabs.add(etab);
    }
}
