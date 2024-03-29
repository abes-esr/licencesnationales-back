package fr.abes.licencesnationales.web.service;

import fr.abes.licencesnationales.web.recaptcha.ReCaptchaKeys;
import fr.abes.licencesnationales.web.recaptcha.ReCaptchaResponse;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Slf4j
@NoArgsConstructor
@Service
public class ReCaptchaService {

    private ReCaptchaKeys reCaptchaKeys;
    private RestTemplate restTemplate;

    @Autowired
    public ReCaptchaService(ReCaptchaKeys reCaptchaKeys, RestTemplate restTemplate){
        this.reCaptchaKeys = reCaptchaKeys;
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    public ReCaptchaResponse verify(String recaptcharesponse, String action) {
        //requête à l'API google
        URI verifGoogleAdr = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",
                        reCaptchaKeys.getSecret(), recaptcharesponse));

        //appel http via post
        ReCaptchaResponse reCaptchaResponse = restTemplate.postForObject(verifGoogleAdr, "", ReCaptchaResponse.class);

        if(reCaptchaResponse!=null){
            if(reCaptchaResponse.isSuccess() && (reCaptchaResponse.getScore() < reCaptchaKeys.getThreshold()
            || !reCaptchaResponse.getAction().equals(action))) {
                reCaptchaResponse.setSuccess(false);
            }

        }

        return reCaptchaResponse;
    }
}
