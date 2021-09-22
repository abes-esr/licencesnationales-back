package fr.abes.licencesnationales.web.dto.authentification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnexionResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private Integer userId;
    private String userSiren;
    private String userNameEtab;
    private String role;

}
