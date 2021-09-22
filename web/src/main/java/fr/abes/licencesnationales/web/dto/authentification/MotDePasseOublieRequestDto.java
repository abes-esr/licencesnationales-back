package fr.abes.licencesnationales.web.dto.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotDePasseOublieRequestDto {

    @JsonProperty("email")
    String email;

    @JsonProperty("siren")
    String siren;

    @JsonProperty("recaptcha")
    String recaptcha;

}
