package fr.abes.licencesnationales.security.payload.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class LoginRequest {

	@NotBlank(message = "SIREN obligatoire (login)")
	@Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir 9 chiffres")
	@ApiModelProperty(value = "identifiant siren", name = "login", dataType = "String", example = "123456789")
	private String login; //siren

	@NotBlank(message = "Mot de passe obligatoire (password)")
	@ApiModelProperty(value = "Mot de passe de l'utilisateur", name = "password", dataType = "String", example = "?Ll2020!")
	private String password;
}
