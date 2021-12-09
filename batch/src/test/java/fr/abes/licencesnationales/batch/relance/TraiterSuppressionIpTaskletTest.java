package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.batch.JobRelanceConfigurationTest;
import fr.abes.licencesnationales.batch.JobRelancesConfig;
import fr.abes.licencesnationales.batch.relance.dto.EtablissementDto;
import fr.abes.licencesnationales.batch.utils.BatchUtil;
import fr.abes.licencesnationales.batch.utils.DateHelper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@SpringBootTest(classes = {JobRelancesConfig.class, JobRelanceConfigurationTest.class})
@EnableAutoConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JobRelancesConfig.class})
public class TraiterSuppressionIpTaskletTest {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IpEventRepository ipEventRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    void cleanUp() {
        this.jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testStepExecution() throws IpException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        StatutIpEntity statutAttestation = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statutAttestation);
        ip1.setDateModification((new GregorianCalendar(2019, 1, 1)).getTime());
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test", statutAttestation);
        ip2.setDateModification((new GregorianCalendar(2020, 10, 10)).getTime());
        IpEntity ip3 = new IpV4(3, "3.3.3.3", "test", statutAttestation);
        ip3.setDateModification((new GregorianCalendar(2020, 10, 10)).getTime());

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn1.ajouterIp(ip3);

        List<EtablissementEntity> listeEtabAvecAuMoinsUneIpAttestation = new ArrayList<>();
        listeEtabAvecAuMoinsUneIpAttestation.add(etabIn1);

        IpSupprimeeEventEntity event = Mockito.mock(IpSupprimeeEventEntity.class);
        Mockito.doNothing().when(Mockito.mock(applicationEventPublisher.getClass())).publishEvent(event);
        Mockito.when(ipEventRepository.save(Mockito.any())).thenReturn(null);

        DateHelper mockDate = new DateHelperTest();
        BatchUtil.load(mockDate);

        JobExecution jobExecution = jobLauncherTestUtils.getJobRepository().createJobExecution("jobRelances", new JobParameters());
        jobExecution.getExecutionContext().put("etabAvecAuMoinsUneIpAttestation", listeEtabAvecAuMoinsUneIpAttestation);

        jobExecution = jobLauncherTestUtils.launchStep("stepTraiterSuppressionIp", jobExecution.getExecutionContext());

        List<EtablissementDto> listeOut = ((List<EtablissementDto>) jobExecution.getExecutionContext().get("etablissementDtos"));

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(1, listeOut.size());
        Assertions.assertEquals("testNom", listeOut.get(0).getNomEtab());
        Assertions.assertEquals(1, listeOut.get(0).getIpsSupprimees().size());
        Assertions.assertEquals("1.1.1.1", listeOut.get(0).getIpsSupprimees().get(0));
        Assertions.assertEquals(2, listeOut.get(0).getIpsAttestation().size());
    }
}
