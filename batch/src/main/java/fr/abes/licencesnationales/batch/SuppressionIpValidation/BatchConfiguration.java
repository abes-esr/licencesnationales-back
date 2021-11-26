package fr.abes.licencesnationales.batch.SuppressionIpValidation;

import fr.abes.licencesnationales.batch.LnBatchConfigurer;
import fr.abes.licencesnationales.batch.SuppressionIpValidation.tasklets.GetIpATraiterTasklet;
import fr.abes.licencesnationales.batch.TimeIncrementer;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.services.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
    private IpService ipService;

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
                .start(stepCalculDateSuppression()).on("SUPTODAY").to(stepSuppressionCompte())
                .on("INFTODAY").end()
                .build().build();
    }
    @Bean
    public Job jobRelances(ItemReader itemReader, ItemProcessor itemProcessor, ItemWriter itemWriter) {
        return this.jobs.get("jobRelances").incrementer(incrementer())
                .start(stepGetIpATraiter()).on("FAILED").end()
                .from(stepGetIpATraiter()).on("COMPLETED").to(stepTraiterSuppressionIp(itemReader, itemProcessor, itemWriter))
                .next(stepSelectIpAttestationAEnvoyer())
                .build().build();
    }

    @Bean
    public Step stepCalculDateSuppression() {
        return stepBuilderFactory.get("stepCalculDateSUppression").allowStartIfComplete(true)
        .tasklet(new CalculDateSuppressionTasklet())
        .build();
    }

    @Bean
    public Step stepSuppressionCompte() {
        return stepBuilderFactory.get("stepSuppressionCompte").allowStartIfComplete(true)
                .tasklet(new SuppressionCompteTasklet())
                .build();
    }

    @Bean
    public Step stepGetIpATraiter() {
        return stepBuilderFactory.get("stepGetIpATraiter").allowStartIfComplete(true)
                .tasklet(new GetIpATraiterTasklet(ipService))
                .build();
    }

    @Bean
    public Step stepTraiterSuppressionIp(ItemReader reader, ItemProcessor processor, ItemWriter writer) {
        return stepBuilderFactory.get("stepTraiterSuppressionIp").<IpDto, IpSupprimeeEventEntity> chunk(chunkSize)
                .reader(reader).processor(processor).writer(writer).build();
    }

    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }


}
