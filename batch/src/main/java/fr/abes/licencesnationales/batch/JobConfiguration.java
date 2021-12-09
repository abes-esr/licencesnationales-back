package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;


public abstract class JobConfiguration {
    @Autowired
    protected EntityManagerFactory entityManagerFactory;
    @Autowired
    protected JobBuilderFactory jobBuilderFactory;
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    @Autowired
    protected ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    protected IpEventRepository ipEventRepository;
    @Autowired
    protected EmailService emailService;
    @Autowired
    protected EventService eventService;
    @Autowired
    protected EtablissementService etablissementService;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LnBatchConfigurer configurer() {
        return new LnBatchConfigurer(entityManagerFactory);
    }

}
