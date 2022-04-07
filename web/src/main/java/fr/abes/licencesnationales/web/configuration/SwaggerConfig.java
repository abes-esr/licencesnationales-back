package fr.abes.licencesnationales.web.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI OpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Licences Nationales")
                        .description("Le service web RESTful permet d'exposer un message public de bienvenue et un message privé aux utilisateurs authentifiés sur le service.</br>" +
                                "Les échanges s'opèrent via des requêtes HTTP contenant des fichiers JSON dans le corps des requêtes (Body) et une clé 'Authorization' dans l'entête des requêtes pour les appels à des fr.abes.licencesnationales.core.services sécurisés.")
                        .version("v0.0.1")
                        .contact(new Contact().url("https://github.com/abes-esr/licencesnationales-back").name("Abes").email("scod@abes.fr")));
    }

}
