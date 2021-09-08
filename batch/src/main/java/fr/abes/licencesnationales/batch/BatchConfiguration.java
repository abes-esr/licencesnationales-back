package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.core.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.batch.traiterSuppressionIpChunk.IpSuppressionProcessor;
import fr.abes.licencesnationales.batch.traiterSuppressionIpChunk.IpSuppressionWriter;
import fr.abes.licencesnationales.core.entities.IpEntity;
import fr.abes.licencesnationales.core.services.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

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
    public BatchConfigurer configurer() {
        return new LnBatchConfigurer(entityManagerFactory);
    }

    @Bean
    public Job jobSupprimerIpEnValidation() {
        return this.jobs.get("supprimerIpEnValidation").incrementer(incrementer()).start(stepTraiterSuppressionIp()).next(envoiMail())
                .build();
    }

    @Bean
    public Step stepTraiterSuppressionIp() {
        return stepBuilderFactory.get("stepTraiterSuppressionIp").<IpEntity, IpSupprimeeEvent>chunk(chunkSize)
                .reader(reader()).processor(processor()).writer(ipWriter()).build();
    }

    @Bean
    public ItemReader reader() {
        Calendar dateJour = new GregorianCalendar();
        dateJour.add(Calendar.YEAR, -1);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date",  dateJour.getTime());
        return new JpaPagingItemReaderBuilder<IpEntity>().name("ipReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select i from IpEntity i where i.validee=0 and i.dateModification < :date")
                .parameterValues(parameters)
                .pageSize(1000)
                .saveState(false)
                .build();
    }

    @Bean
    @StepScope
    public IpSuppressionProcessor processor() {
        return new IpSuppressionProcessor();
    }

    @Bean
    @StepScope
    public ItemWriter ipWriter() {
        return new IpSuppressionWriter(ipService);
    }

    @Bean
    @StepScope
    public Step envoiMail() {
        return stepBuilderFactory.get("envoiMail").tasklet(envoiMailTasklet()).build();
    }

    public Tasklet envoiMailTasklet() {
        return new EnvoiMailTasklet();
    }

    protected JobParametersIncrementer incrementer() {
        return new TimeIncrementer();
    }


}
