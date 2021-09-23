package fr.abes.licencesnationales.web.dto.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnexionResponseDto {

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("tokenType")
    private String tokenType = "Bearer";

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("userSiren")
    private String userSiren;

    @JsonProperty("userNameEtab")
    private String userNameEtab;

    @JsonProperty("role")
    private String role;

}
