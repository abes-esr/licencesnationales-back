package fr.abes.licencesnationales.batch.relance.tasklets;

import fr.abes.licencesnationales.batch.JobRelancesConfig;
import fr.abes.licencesnationales.batch.relance.JobConfigurationTest;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JobConfigurationTest.class, JobRelancesConfig.class}, properties = "spring.batch.job.enabled=false")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConstructionListeEtabTaskletTest {
    @Autowired
    @InjectMocks
    private JobLauncherTestUtils launcherGestionCompte;
    @MockBean
    private Tasklet tasklet;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @MockBean(name = "mockEtabService")
    private EtablissementService mockEtabService;

    private List<EtablissementEntity> listIn;

    @BeforeEach
    public void init() {
        this.jobRepositoryTestUtils.removeJobExecutions();

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact2);

        listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        Mockito.when(mockEtabService.findAll()).thenReturn(listIn);
    }

    @Test
    public void testTasklet() {
        JobExecution jobExecution = launcherGestionCompte.launchStep("stepContructionListeEtab");

        Collection actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualExitStatus = jobExecution.getExitStatus();
        ExecutionContext actualContext = jobExecution.getExecutionContext();

        Assertions.assertEquals(1, actualStepExecutions.size());
        Assertions.assertEquals("COMPLETED", actualExitStatus.getExitCode());
        Assertions.assertEquals(2, ((List<EtablissementEntity>)actualContext.get("etabSansIp")).size());
    }
}
