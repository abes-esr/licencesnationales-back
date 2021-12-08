package fr.abes.licencesnationales.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.StringUtils;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"fr.abes.licencesnationales.batch", "fr.abes.licencesnationales.core"})
@EntityScan("fr.abes.licencesnationales.core.entities")
@EnableJpaRepositories(basePackages = "fr.abes.licencesnationales.core.repository")
@EnableBatchProcessing
public class BatchApplication {

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        long startTime = System.currentTimeMillis();
        ConfigurableApplicationContext ctx = SpringApplication.run(BatchApplication.class, args);

        JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
        jobLauncher.run((Job) ctx.getBean("jobGestionCompte"), new JobParameters());
        jobLauncher.run((Job) ctx.getBean("jobRelances"), new JobParameters());

        long endTime = System.currentTimeMillis();
        log.debug("Timing " + (endTime - startTime) + " ms");
        System.exit(SpringApplication.exit(ctx));
    }
}
