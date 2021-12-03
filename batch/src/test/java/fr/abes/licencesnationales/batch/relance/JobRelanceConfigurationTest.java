package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
public class JobRelanceConfigurationTest {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    @InjectMocks
    private Job jobRelances;

    @MockBean
    public EtablissementService service;

    @MockBean
    public EtablissementRepository etablissementRepository;

    @MockBean
    public IpEventRepository ipEventRepository;

    @MockBean
    public ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    public EmailService emailService;

    @MockBean
    public EventService eventService;

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
