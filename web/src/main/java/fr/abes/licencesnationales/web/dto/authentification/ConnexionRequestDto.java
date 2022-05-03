package fr.abes.licencesnationales.web.dto.authentification;

import fr.abes.licencesnationales.core.constant.Constant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class ConnexionRequestDto {

	@NotBlank(message = Constant.ERROR_ETAB_SIREN_OBLIGATOIRE)
    @Pattern(regexp = "^\\d{9}$", message = Constant.SIREN_DOIT_CONTENIR_9_CHIFFRES)
    @Schema(description = "identifiant siren", name = "login", type = "String", example = "123456789")
	private String login; //siren

    @NotBlank(message = Constant.ERROR_ETAB_MDP_OBLIGATOIRE)
    @Schema(description = "Mot de passe de l'utilisateur", name = "password", type = "String", example = "?Ll2020!")
	private String password;
}
