package fr.abes.licencesnationales.web.dto.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter @Setter
public class ModifierMotDePasseRequestDto {

    @JsonProperty("ancienMotDePasse")
    private String ancienMotDePasse;

    @JsonProperty("nouveauMotDePasse")
    @Size(min = 8, message = "Votre mot de passe doit contenir au minimum 8 caractères dont une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial parmis @ $ ! % * ? &")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Votre mot de passe doit contenir au minimum 8 caractères dont une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial parmis @ $ ! % * ? &")
    private String nouveauMotDePasse;

}