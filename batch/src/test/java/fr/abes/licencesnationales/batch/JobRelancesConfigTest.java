package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.relance.ConstructionListeEtabTasklet;
import fr.abes.licencesnationales.batch.relance.TraiterEtabSansIpTasklet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.*;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {JobRelancesConfig.class, JobRelanceConfigurationTest.class})
@EnableAutoConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JobRelancesConfig.class})
public class JobRelancesConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    void clear() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJobRelance() throws Exception {
        ConstructionListeEtabTasklet tasklet = Mockito.mock(ConstructionListeEtabTasklet.class);
        Mockito.when(tasklet.execute(Mockito.any(), Mockito.any())).thenReturn(RepeatStatus.FINISHED);
        JobExecution jobExecution =  new JobExecution(new JobInstance(1L, "jobRelances"), new JobParameters());
        StepExecution stepExecution = new StepExecution("stepContructionListeEtab", jobExecution);

        stepExecution.setExitStatus(new ExitStatus("AUCUNETAB"));

        jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        Assertions.assertEquals("jobRelances", actualJobInstance.getJobName());
        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("STOPPED", actualJobExitStatus);
    }

    @Test
    void testJobRelance2() throws Exception {
        JobInstanceDao jobInstanceDao = new JdbcJobInstanceDao();
        JobExecutionDao jobExecutionDao = new JdbcJobExecutionDao();
        StepExecutionDao stepExecutionDao = new JdbcStepExecutionDao();
        ExecutionContextDao ec = new JdbcExecutionContextDao();
        JobRepository jobRepository = new SimpleJobRepository(jobInstanceDao, jobExecutionDao, stepExecutionDao, ec);
        SimpleJob job = new SimpleJob("jobRelances");

        StubStep stepEtab = new StubStep("stepContructionListeEtab", jobRepository);
        StubStep stepSansIp = new StubStep("stepTraiterEtabSansIp", jobRepository);
        List<Step> steps = new ArrayList<>();
        steps.add(stepEtab);
        steps.add(stepSansIp);
        job.setSteps(steps);

        JobExecution jobExecution = jobRepository.createJobExecution(job.getName(), new JobParameters());
        JobInstance jobInstance = jobExecution.getJobInstance();
        jobLauncherTestUtils.setJobRepository(jobRepository);

        jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        Assertions.assertEquals("jobRelances", actualJobInstance.getJobName());
        Assertions.assertEquals(2, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus);
    }

    private static class StubStep extends org.springframework.batch.core.step.AbstractStep {

        private Runnable runnable;

        private Throwable exception;

        private JobRepository jobRepository;

        private ExecutionContext passedInStepContext;

        private ExecutionContext passedInJobContext;

        /**
         * @param string
         */
        public StubStep(String string, JobRepository jobRepository) {
            super(string);
            this.jobRepository = jobRepository;
        }

        /**
         * @param exception
         */
        public void setProcessException(Throwable exception) {
            this.exception = exception;
        }

        /**
         * @param runnable
         */
        public void setCallback(Runnable runnable) {
            this.runnable = runnable;
        }

        /*
         * (non-Javadoc)
         *
         * @seeorg.springframework.batch.core.step.StepSupport#execute(org.
         * springframework.batch.core.StepExecution)
         */
        @Override
        public void doExecute(StepExecution stepExecution) throws JobInterruptedException,
                UnexpectedJobExecutionException {

            passedInJobContext = new ExecutionContext(stepExecution.getJobExecution().getExecutionContext());
            passedInStepContext = new ExecutionContext(stepExecution.getExecutionContext());
            stepExecution.getExecutionContext().putString("stepKey", "stepValue");
            stepExecution.getJobExecution().getExecutionContext().putString("jobKey", "jobValue");
            jobRepository.update(stepExecution);
            jobRepository.updateExecutionContext(stepExecution);

            if (exception instanceof JobInterruptedException) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(((JobInterruptedException) exception).getStatus());
                stepExecution.addFailureException(exception);
                throw (JobInterruptedException) exception;
            }
            if (exception instanceof RuntimeException) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(BatchStatus.FAILED);
                stepExecution.addFailureException(exception);
                return;
            }
            if (exception instanceof Error) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(BatchStatus.FAILED);
                stepExecution.addFailureException(exception);
                return;
            }
            if (exception instanceof JobInterruptedException) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(BatchStatus.FAILED);
                stepExecution.addFailureException(exception);
                return;
            }
            if (runnable != null) {
                runnable.run();
            }
            stepExecution.setExitStatus(ExitStatus.COMPLETED);
            stepExecution.setStatus(BatchStatus.COMPLETED);
            jobRepository.update(stepExecution);
            jobRepository.updateExecutionContext(stepExecution);

        }

    }
}
