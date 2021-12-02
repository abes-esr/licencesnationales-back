package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableBatchProcessing
public abstract class JobConfiguration {
    @Autowired
    protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    protected EtablissementService service;
    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    protected IpEventRepository ipEventRepository;

    public JobConfiguration() {
        super();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }

}
