package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.gestionCompte.SelectEtablissementTasklet;
import fr.abes.licencesnationales.batch.gestionCompte.SuppressionEtEnvoiMailTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobGestionCompteConfig extends JobConfiguration {

    @Bean(name = "jobGestionCompte")
    public Job jobGestionCompte() {
        return this.jobBuilderFactory.get("gestionCompte").incrementer(new RunIdIncrementer())
                .start(stepSelectEtablissement()).on("AUCUNETAB").stop()
                .on("COMPLETED").to(stepSuppressionCompte())
                .build().build();
    }

    /***** Steps Job Gestion de compte *****/
    @Bean
    public Step stepSelectEtablissement() {
        return stepBuilderFactory.get("stepSelectEtablissement").allowStartIfComplete(true)
        .tasklet(new SelectEtablissementTasklet(etablissementService))
        .build();
    }

    @Bean
    public Step stepSuppressionCompte() {
        return stepBuilderFactory.get("stepSuppressionCompte").allowStartIfComplete(true)
                .tasklet(new SuppressionEtEnvoiMailTasklet(etablissementService, emailService, mailAdmin))
                .build();
    }
}
