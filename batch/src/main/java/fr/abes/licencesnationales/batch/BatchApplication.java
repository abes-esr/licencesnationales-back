package fr.abes.licencesnationales.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"fr.abes.licencesnationales.batch","fr.abes.licencesnationales.core"})
@EntityScan("fr.abes.licencesnationales.core")
@EnableJpaRepositories(basePackages = "fr.abes.licencesnationales.core.repository")
public class BatchApplication {

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        long startTime = System.currentTimeMillis();
        ApplicationContext ctx = SpringApplication.run(BatchApplication.class, args);
        JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
        JobExecution executionJobGestionCompte = jobLauncher.run((Job) ctx.getBean("jobGestionCompte"), null);
        if (ExitStatus.COMPLETED.equals(executionJobGestionCompte.getExitStatus())) {
            jobLauncher.run((Job) ctx.getBean("jobRelances"), null);
        }

        long endTime = System.currentTimeMillis();
        log.debug("Timing " + (endTime-startTime) +" ms");
        System.exit(0);
    }
}
