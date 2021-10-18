package fr.abes.licencesnationales.web.dto.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.licencesnationales.core.constant.Constant;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter @Setter
public class ModifierMotDePasseRequestDto {
    @JsonProperty("ancienMotDePasse")
    private String ancienMotDePasse;

    @JsonProperty("nouveauMotDePasse")
    @Size(min = 8, message = Constant.MESSAGE_REGEXP_PASSWORD)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = Constant.MESSAGE_REGEXP_PASSWORD)
    private String nouveauMotDePasse;

}
