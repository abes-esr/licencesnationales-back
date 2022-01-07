package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.batch.JobRelanceConfigurationTest;
import fr.abes.licencesnationales.batch.JobRelancesConfig;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SpringBootTest(classes = {JobRelancesConfig.class, JobRelanceConfigurationTest.class})
@EnableAutoConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JobRelancesConfig.class})
public class TraiterEtabSansIpTaskletTest {


    @Autowired
    private EmailService emailService;

    @Autowired
    private EventService eventService;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    void cleanUp() {
        this.jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testTaskletTraiteEtabSansIp() throws Exception {
        Mockito.doNothing().when(emailService).constructRelanceEtabMailUser(Mockito.anyString(), Mockito.anyString());
        Mockito.when(eventService.getLastDateSuppressionIpEtab(Mockito.any())).thenReturn(null);
        Mockito.when(eventService.getDateCreationEtab(Mockito.any())).thenReturn(Calendar.getInstance().getTime());

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);
        List<EtablissementEntity> etabSansIp = new ArrayList<>();
        etabSansIp.add(etabIn1);

        JobExecution jobExecution = jobLauncherTestUtils.getJobRepository().createJobExecution("jobRelances", new JobParameters());
        jobExecution.getExecutionContext().put("etabSansIp", etabSansIp);
        jobExecution = jobLauncherTestUtils.launchStep("stepTraiterEtabSansIp", jobExecution.getExecutionContext());

        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
