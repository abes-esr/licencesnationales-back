package fr.abes.licencesnationales.web.dto.password;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    @JsonProperty("jwtToken")
    private String token;
}
