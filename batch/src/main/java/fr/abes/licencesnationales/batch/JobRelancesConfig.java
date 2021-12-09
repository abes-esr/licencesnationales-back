package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.relance.ConstructionListeEtabTasklet;
import fr.abes.licencesnationales.batch.relance.EnvoiMailRelanceTasklet;
import fr.abes.licencesnationales.batch.relance.TraiterEtabSansIpTasklet;
import fr.abes.licencesnationales.batch.relance.TraiterSuppressionIpTasklet;
import fr.abes.licencesnationales.batch.utils.DateHelperImpl;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobRelancesConfig extends JobConfiguration {

    @Bean(name = "jobRelances")
    public Job jobRelances() {
        return this.jobBuilderFactory.get("jobRelances").incrementer(new RunIdIncrementer())
                .start(stepConstructionListeEtab()).on("ETABSANSIPONLY").to(etabSansIpOnly())
                .from(stepConstructionListeEtab()).on("ETABAVECAUMOINSUNEIPATTESTATIONONLY").to(etabAvecAuMoinsUneIpAttestation())
                .from(stepConstructionListeEtab()).on("ALLTYPEETAB").to(allTypeEtab())
                .from(stepConstructionListeEtab()).on("AUCUNETAB").end()
                .build().build();
    }

    //flow
    @Bean
    public Flow etabSansIpOnly() {
        return new FlowBuilder<Flow>("etabSansIpOnly")
                .start(stepTraiterEtabSansIp())
                .build();
    }

    @Bean
    public Flow etabAvecAuMoinsUneIpAttestation() {
        return new FlowBuilder<Flow>("etabAvecAuMoinsUneIpAttestation")
                .start(stepTraiterSuppressionIp()).next(stepEnvoiMailRelance())
                .build();
    }

    @Bean
    public Flow allTypeEtab() {
        return new FlowBuilder<Flow>("allTypeEtab")
                .start(stepTraiterEtabSansIp()).next(stepTraiterSuppressionIp()).next(stepEnvoiMailRelance())
                .build();
    }

    /***** Steps relance *****/
    @Bean
    public Step stepConstructionListeEtab() {
        return stepBuilderFactory.get("stepConstructionListeEtab").allowStartIfComplete(true)
                .tasklet(new ConstructionListeEtabTasklet(etablissementService))
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
                .tasklet(new EnvoiMailRelanceTasklet(emailService))
                .build();
    }
}
