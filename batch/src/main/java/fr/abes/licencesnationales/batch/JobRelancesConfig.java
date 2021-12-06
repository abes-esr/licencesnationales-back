package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.relance.ConstructionListeEtabTasklet;
import fr.abes.licencesnationales.batch.relance.EnvoiMailRelanceTasklet;
import fr.abes.licencesnationales.batch.relance.TraiterEtabSansIpTasklet;
import fr.abes.licencesnationales.batch.relance.TraiterSuppressionIpTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRelancesConfig extends JobConfiguration {
    @Bean(name = "jobRelances")
    public Job jobRelances() {
        return this.jobBuilderFactory.get("jobRelances").incrementer(new RunIdIncrementer())
                .start(stepContructionListeEtab()).next(stepTraiterEtabSansIp())
                .next(stepTraiterSuppressionIp())
                .next(stepEnvoiMailRelance())
                .build();
    }

    /***** Steps relance *****/
    @Bean
    public Step stepContructionListeEtab() {
        return stepBuilderFactory.get("stepContructionListeEtab").allowStartIfComplete(true)
                .tasklet(new ConstructionListeEtabTasklet(service))
                .build();
    }

    @Bean
    public Step stepTraiterEtabSansIp() {
        return stepBuilderFactory.get("stepTraiterEtabSansIp").allowStartIfComplete(true)
                .tasklet(new TraiterEtabSansIpTasklet(emailService, eventService))
                .build();
    }

    @Bean
    public Step stepTraiterSuppressionIp() {
        return stepBuilderFactory.get("stepTraiterSuppressionIp").allowStartIfComplete(true)
                .tasklet(new TraiterSuppressionIpTasklet(ipEventRepository, applicationEventPublisher))
                .build();
    }

    @Bean
    public Step stepEnvoiMailRelance() {
        return stepBuilderFactory.get("stepEnvoiMailRelance").allowStartIfComplete(true)
                .tasklet(new EnvoiMailRelanceTasklet())
                .build();
    }
}
