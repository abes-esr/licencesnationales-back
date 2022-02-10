package fr.abes.licencesnationales.web.dto.authentification;

import fr.abes.licencesnationales.core.constant.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class ConnexionRequestDto {

	@NotBlank(message = Constant.ERROR_ETAB_SIREN_OBLIGATOIRE)
	@Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir 9 chiffres")
	@ApiModelProperty(value = "identifiant siren", name = "login", dataType = "String", example = "123456789")
	private String login; //siren

	@NotBlank(message = "Mot de passe obligatoire (password)")
	@ApiModelProperty(value = "Mot de passe de l'utilisateur", name = "password", dataType = "String", example = "?Ll2020!")
	private String password;
}
