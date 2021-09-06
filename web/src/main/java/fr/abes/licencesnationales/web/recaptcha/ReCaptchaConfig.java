package fr.abes.licencesnationales.web.recaptcha;



import fr.abes.licencesnationales.web.service.ReCaptchaService;
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
    ReCaptchaService ReCaptchaService(){
        return new ReCaptchaService();
    }
    @Bean
    ReCaptchaKeys recaptchaKeys () {
        return new ReCaptchaKeys();
    }

}
