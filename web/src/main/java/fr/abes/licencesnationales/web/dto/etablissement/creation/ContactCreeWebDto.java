package fr.abes.licencesnationales.web.dto.etablissement.creation;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.licencesnationales.web.dto.etablissement.ContactWebDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ContactCreeWebDto extends ContactWebDto {
    @JsonProperty("motDePasse")
    @NotNull
    private String motDePasse;

}
