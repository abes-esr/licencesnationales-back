package fr.abes.licencesnationales.web.service;

import fr.abes.licencesnationales.web.recaptcha.ReCaptchaKeys;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.net.URI;


@Slf4j
@NoArgsConstructor
public class ReCaptchaService {

    private ReCaptchaKeys reCaptchaKeys;
    private RestTemplate restTemplate;

    @Autowired
    public ReCaptchaService(ReCaptchaKeys reCaptchaKeys, RestTemplate restTemplate){
        this.reCaptchaKeys = reCaptchaKeys;
        this.restTemplate = restTemplate;
    }

    public ReCaptchaResponse verify(String recaptcharesponse, String action) {

        log.info("debut ReCaptchaService - verify()");
        log.info("response = " + recaptcharesponse);
        log.info("reCaptchaKeys.getSecret() = " + reCaptchaKeys.getSecret());

        //requête à l'API google
        URI verifGoogleAdr = URI.create
        (String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",
                reCaptchaKeys.getSecret(), recaptcharesponse));

        //appel http via rest
        ReCaptchaResponse reCaptchaResponse = restTemplate.getForObject(verifGoogleAdr, ReCaptchaResponse.class);
        log.info("reCaptchaResponse = " + reCaptchaResponse);

        if(reCaptchaResponse!=null){
            if(reCaptchaResponse.isSuccess() && (reCaptchaResponse.getScore() < reCaptchaKeys.getThreshold()
            || !reCaptchaResponse.getAction().equals(action))) {
                reCaptchaResponse.setSuccess(false);
            }

        }

        return reCaptchaResponse;
    }
}
