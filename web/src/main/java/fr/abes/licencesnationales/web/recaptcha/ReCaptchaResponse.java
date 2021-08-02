package fr.abes.licencesnationales.web.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class ReCaptchaResponse {


    private boolean success;
    private float score;
    private String action;
    @JsonProperty("challenge_ts")
    private String challengeTs;
    private String hostname;
    @JsonProperty("error-codes")
    List<String> errorCodes;

    public List<String> getErrors(){
        if(getErrorCodes()!=null) {
            return getErrorCodes().stream()
                    .map(ReCaptchaErrorCodes.RECAPTCHA_ERROR_CODES::get)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public String toString() {
        return "ReCaptchaResponse{" +
                "success=" + success +
                ", score=" + score +
                ", action='" + action + '\'' +
                ", challengeTs='" + challengeTs + '\'' +
                ", hostname='" + hostname + '\'' +
                ", errorCodes=" + errorCodes +
                '}';
    }

}
