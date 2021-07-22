package fr.abes.licencesnationales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un exemplaire de Notice au format JSON de l'API
 */
@Getter @Setter
public class PasswordWebDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("jwtToken")
    private String token;

    @JsonProperty("motDePasse")
    private String password;

    @JsonProperty("oldPassword")
    private String oldPassword;

    @JsonProperty("newPassword")
    private String newPassword;

    @JsonProperty("fr/abes/licencesnationales/recaptcha")
    private String repatcha;

}
