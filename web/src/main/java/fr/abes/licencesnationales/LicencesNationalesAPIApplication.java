package fr.abes.licencesnationales;

import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class LicencesNationalesAPIApplication extends SpringBootServletInitializer implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LicencesNationalesAPIApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(LicencesNationalesAPIApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }

    @Bean
    public FilterRegistrationBean simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // *** URL below needs to match the Vue client URL and port ***
        
        // Config for allowing CORS for the local, dev, test, prod internal servers
        // not necessary to set anything for production URL because de interne domain/port
        // is the same for licencesnationales-back and licencesnationales-front:
        // https://acces.licencesnationales.fr/ => for the front
        // https://acces.licencesnationales.fr/v1/ => for the back (api)
        List<String> allowedOriginsUrl = new ArrayList<>();
        allowedOriginsUrl.add("http://localhost:8080");
        allowedOriginsUrl.add("http://localhost:8081");
        allowedOriginsUrl.add("http://127.0.0.1:8081");
        allowedOriginsUrl.add("http://127.0.0.1:6081");
        allowedOriginsUrl.add("http://diplotaxis-test.v202.abes.fr:6080");
        allowedOriginsUrl.add("http://diplotaxis-dev.v212.abes.fr:6080");
        allowedOriginsUrl.add("http://diplotaxis-prod.v102.abes.fr:6080");
        config.setAllowedOrigins(allowedOriginsUrl);
        config.setAllowedOriginPatterns(Collections.singletonList("https://*.licencesnationales.fr"));

        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
