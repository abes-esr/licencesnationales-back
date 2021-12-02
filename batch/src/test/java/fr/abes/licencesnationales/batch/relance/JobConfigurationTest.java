package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.core.services.EtablissementService;
import org.mockito.Mockito;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EnableAutoConfiguration
@EnableBatchProcessing
@ComponentScan(basePackages = {"fr.abes.licencesnationales.core"})
@EnableJpaRepositories(basePackages = "fr.abes.licencesnationales.core.repository")
@EntityScan("fr.abes.licencesnationales.core.entities")
public class JobConfigurationTest {
    @Bean(name = "mockEtabService")
    @Primary
    public EtablissementService mockEtabService(){
        return Mockito.mock(EtablissementService.class);
    }
}
