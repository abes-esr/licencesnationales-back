package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.relance.IpDto;
import fr.abes.licencesnationales.batch.relance.tasklets.EnvoiMailRelanceTasklet;
import fr.abes.licencesnationales.batch.relance.tasklets.ConstructionListeEtabTasklet;
import fr.abes.licencesnationales.batch.gestionCompte.SelectEtablissementTasklet;
import fr.abes.licencesnationales.batch.gestionCompte.SuppressionEtEnvoiMailTasklet;
import fr.abes.licencesnationales.batch.relance.tasklets.SelectionEtabARelanceTasklet;
import fr.abes.licencesnationales.batch.relance.tasklets.TraiterEtabSansIpTasklet;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("${batch.chunkSize}")
    private Integer chunkSize;

    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private EtablissementService etablissementService;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BatchConfigurer configurer() {
        return new LnBatchConfigurer(entityManagerFactory);
    }

    @Bean
    public Job jobGestionCompte() {
        return this.jobs.get("gestionCompte").incrementer(incrementer())
                .start(stepSelectEtablissement()).next(stepSuppressionCompte())
                .build();
    }

    @Bean
    public Job jobRelances(ItemReader itemReader, ItemProcessor itemProcessor, ItemWriter itemWriter) {
        return this.jobs.get("jobRelances").incrementer(incrementer())
                .start(stepContructionListeEtab()).next(stepTraiterEtabSansIp())
                .next(stepTraiterSuppressionIp(itemReader, itemProcessor, itemWriter))
                .next(stepSelectionEtabARelancer())
                .next(stepEnvoiMailRelance())
                .build();
    }

    /***** Steps Job Gestion de compte *****/
    @Bean
    public Step stepSelectEtablissement() {
        return stepBuilderFactory.get("stepCalculDateSUppression").allowStartIfComplete(true)
        .tasklet(new SelectEtablissementTasklet())
        .build();
    }

    @Bean
    public Step stepSuppressionCompte() {
        return stepBuilderFactory.get("stepSuppressionCompte").allowStartIfComplete(true)
                .tasklet(new SuppressionEtEnvoiMailTasklet())
                .build();
    }

    /***** Steps relance *****/
    @Bean
    public Step stepContructionListeEtab() {
        return stepBuilderFactory.get("stepContructionListeEtab").allowStartIfComplete(true)
                .tasklet(new ConstructionListeEtabTasklet(etablissementService))
                .build();
    }

    @Bean
    public Step stepTraiterEtabSansIp() {
        return stepBuilderFactory.get("stepTraiterEtabSansIp").allowStartIfComplete(true)
                .tasklet(new TraiterEtabSansIpTasklet())
                .build();
    }
    
    @Bean
    public Step stepTraiterSuppressionIp(ItemReader reader, ItemProcessor processor, ItemWriter writer) {
        return stepBuilderFactory.get("stepTraiterSuppressionIp").<IpDto, IpSupprimeeEventEntity> chunk(chunkSize)
                .reader(reader).processor(processor).writer(writer).build();
    }
    
    @Bean
    public Step stepSelectionEtabARelancer() {
        return stepBuilderFactory.get("stepSelectionEtabARelancer").allowStartIfComplete(true)
                .tasklet(new SelectionEtabARelanceTasklet())
                .build();
    }
    
    @Bean
    public Step stepEnvoiMailRelance() {
        return stepBuilderFactory.get("stepEnvoiMailRelance").allowStartIfComplete(true)
                .tasklet(new EnvoiMailRelanceTasklet())
                .build();
    }
    

    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }


}
