package fr.abes.licencesnationales.web.dto.password;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordResetWebDto {
    @JsonProperty("email")
    String email;
    @JsonProperty("siren")
    String siren;

}
