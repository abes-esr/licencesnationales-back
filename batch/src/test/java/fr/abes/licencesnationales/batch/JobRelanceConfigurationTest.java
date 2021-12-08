package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.utils.DateHelperImpl;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import org.mockito.InjectMocks;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRelanceConfigurationTest {
    @MockBean
    public ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    public EtablissementService etablissementService;

    @MockBean
    public IpEventRepository ipEventRepository;

    @MockBean
    public EmailService emailService;

    @MockBean
    public EventService eventService;

    @MockBean
    public DateHelperImpl dateHelper;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @InjectMocks
    private Job jobRelances;

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils(){
        final JobLauncherTestUtils utils = new JobLauncherTestUtils();
        utils.setJobLauncher(jobLauncher);
        utils.setJobRepository(jobRepository);
        utils.setJob(jobRelances);
        return utils;
    }

    @Bean
    public JobRepositoryTestUtils jobRepositoryTestUtils() throws Exception {
        final JobRepositoryTestUtils utils = new JobRepositoryTestUtils();
        utils.setJobRepository(jobRepository);
        return utils;
    }

}
