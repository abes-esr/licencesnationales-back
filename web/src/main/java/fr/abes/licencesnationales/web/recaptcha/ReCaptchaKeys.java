package fr.abes.licencesnationales.web.recaptcha;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class ReCaptchaKeys {

    @Value("${google.fr.abes.licencesnationales.recaptcha.key.site}")
    private String site;

    @Value("${google.fr.abes.licencesnationales.recaptcha.key.secret}")
    private String secret;

    @Value("${google.fr.abes.licencesnationales.recaptcha.key.threshold}")
    private float threshold;
}
