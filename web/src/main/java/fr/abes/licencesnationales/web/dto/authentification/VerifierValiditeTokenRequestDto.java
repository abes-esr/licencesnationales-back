package fr.abes.licencesnationales.web.dto.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifierValiditeTokenRequestDto {

    @JsonProperty("token")
    private String token;
}
