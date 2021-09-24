package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
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
