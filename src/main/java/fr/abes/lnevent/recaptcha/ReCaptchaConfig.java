package fr.abes.lnevent.recaptcha;


import fr.abes.lnevent.services.ReCaptchaCreationCompteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ReCaptchaConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    ReCaptchaCreationCompteService ReCaptchaCreationCompteService(){
        return new ReCaptchaCreationCompteService();
    }
    @Bean
    ReCaptchaKeys recaptchaKeys () {
        return new ReCaptchaKeys();
    }

}
