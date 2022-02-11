package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.batch.relance.ConstructionListeEtabTasklet;
import fr.abes.licencesnationales.batch.relance.DateHelperTest;
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
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {JobRelancesConfig.class, JobRelanceConfigurationTest.class})
@EnableAutoConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JobRelancesConfig.class})
public class JobRelancesConfigTest {
    @Autowired
    private EtablissementService etablissementService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IpEventRepository ipEventRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    void clear() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @DisplayName("test cas aucun établissement")
    @Test
    void testJobRelanceAucunEtab() throws Exception {
        ConstructionListeEtabTasklet tasklet = Mockito.mock(ConstructionListeEtabTasklet.class);
        Mockito.when(tasklet.execute(Mockito.any(), Mockito.any())).thenReturn(RepeatStatus.FINISHED);
        JobExecution jobExecution =  new JobExecution(new JobInstance(1L, "jobRelances"), new JobParameters());
        StepExecution stepExecution = new StepExecution("stepContructionListeEtab", jobExecution);

        stepExecution.setExitStatus(new ExitStatus("AUCUNETAB"));

        jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        List<StepExecution> steps = jobExecution.getStepExecutions().stream().collect(Collectors.toList());
        StepExecution step1 = steps.get(0);
        Assertions.assertEquals("jobRelances", actualJobInstance.getJobName());
        Assertions.assertEquals(1, steps.size());
        Assertions.assertEquals("AUCUNETAB",step1.getExitStatus().getExitCode());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
    }

    @DisplayName("test établissement sans IP uniquement")
    @Test
    void testJobRelanceEtabSansIpUniquement() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom", "000000000", type, "12345", contact2);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        Mockito.when(etablissementService.findAll()).thenReturn(listIn);
        Mockito.doNothing().when(emailService).constructRelanceEtabMailUser(Mockito.anyString(), Mockito.anyString());
        Mockito.when(eventService.getLastDateSuppressionIpEtab(Mockito.any())).thenReturn(null);
        Mockito.when(eventService.getDateCreationEtab(Mockito.any())).thenReturn(Calendar.getInstance().getTime());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        StepExecution step1 = jobExecution.getStepExecutions().stream().collect(Collectors.toList()).get(0);
        StepExecution step2 = jobExecution.getStepExecutions().stream().collect(Collectors.toList()).get(1);
        Assertions.assertEquals("jobRelances", actualJobInstance.getJobName());
        Assertions.assertEquals(2, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("stepConstructionListeEtab", step1.getStepName());
        Assertions.assertEquals("ETABSANSIPONLY", step1.getExitStatus().getExitCode());
        Assertions.assertEquals("stepTraiterEtabSansIp", step2.getStepName());
        Assertions.assertEquals("COMPLETED", step2.getExitStatus().getExitCode());
        Assertions.assertEquals(2, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
    }

    @DisplayName("test établissement IP attestation uniquement")
    @Test
    void testJobRelanceEtabIpAttestationUniquement() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "000000000", type, "12345", contact2);

        StatutIpEntity statutAttestation = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        StatutIpEntity statutNouvelleIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "Nouvelle IP");

        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statutAttestation);
        ip1.setDateModification((new GregorianCalendar(2019, 1, 1)).getTime());
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test", statutNouvelleIp);
        IpEntity ip3 = new IpV4(3, "3.3.3.3", "test", statutNouvelleIp);

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn2.ajouterIp(ip3);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        IpSupprimeeEventEntity event = Mockito.mock(IpSupprimeeEventEntity.class);
        Mockito.doNothing().when(Mockito.mock(applicationEventPublisher.getClass())).publishEvent(event);
        Mockito.when(ipEventRepository.save(Mockito.any())).thenReturn(null);

        Mockito.when(etablissementService.findAll()).thenReturn(listIn);
        Mockito.doNothing().when(emailService).constructRelanceEtabMailUser(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(emailService).constructSuppresionIpMail(Mockito.any(), Mockito.anyList(), Mockito.anyString(), Mockito.anyString());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        List<StepExecution> steps = jobExecution.getStepExecutions().stream().collect(Collectors.toList());
        StepExecution step1 = steps.get(0);
        StepExecution step2 = steps.get(1);
        StepExecution step3 = steps.get(2);
        Assertions.assertEquals("jobRelances", actualJobInstance.getJobName());
        Assertions.assertEquals(3, steps.size());
        Assertions.assertEquals("stepConstructionListeEtab", step1.getStepName());
        Assertions.assertEquals("ETABAVECAUMOINSUNEIPATTESTATIONONLY", step1.getExitStatus().getExitCode());
        Assertions.assertEquals("stepTraiterSuppressionIp", step2.getStepName());
        Assertions.assertEquals("COMPLETED", step2.getExitStatus().getExitCode());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
        Assertions.assertEquals("stepEnvoiMailRelance", step3.getStepName());
        Assertions.assertEquals(1, ((List<EtablissementDto>) jobExecution.getExecutionContext().get("etablissementDtos")).size());
        Assertions.assertEquals("COMPLETED", step3.getExitStatus().getExitCode());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
    }

    @DisplayName("test établissement IP attestation + etablissement sans IP")
    @Test
    void testJobRelanceEtabIpAttestationAndEtabSansIp() throws Exception {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "000000000", type, "12345", contact2);

        ContactEntity contact3 = new ContactEntity(3, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn3 = new EtablissementEntity(3, "testNom3", "000000000", type, "12345", contact2);

        StatutIpEntity statutAttestation = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        StatutIpEntity statutNouvelleIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "Nouvelle IP");

        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statutAttestation);
        ip1.setDateModification((new GregorianCalendar(2019, 1, 1)).getTime());
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test", statutNouvelleIp);
        IpEntity ip3 = new IpV4(3, "3.3.3.3", "test", statutNouvelleIp);

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn2.ajouterIp(ip3);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);
        listIn.add(etabIn3);

        IpSupprimeeEventEntity event = Mockito.mock(IpSupprimeeEventEntity.class);
        Mockito.doNothing().when(Mockito.mock(applicationEventPublisher.getClass())).publishEvent(event);
        Mockito.when(ipEventRepository.save(Mockito.any())).thenReturn(null);

        DateHelper mockDate = new DateHelperTest();
        BatchUtil.load(mockDate);

        Mockito.when(etablissementService.findAll()).thenReturn(listIn);
        Mockito.doNothing().when(emailService).constructRelanceEtabMailUser(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(emailService).constructSuppresionIpMail(Mockito.any(), Mockito.anyList(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(eventService.getLastDateSuppressionIpEtab(Mockito.any())).thenReturn(null);
        Mockito.when(eventService.getDateCreationEtab(Mockito.any())).thenReturn(Calendar.getInstance().getTime());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        List<StepExecution> steps = jobExecution.getStepExecutions().stream().collect(Collectors.toList());
        StepExecution step1 = steps.get(0);
        StepExecution step2 = steps.get(1);
        StepExecution step3 = steps.get(2);
        StepExecution step4 = steps.get(3);
        Assertions.assertEquals("jobRelances", actualJobInstance.getJobName());
        Assertions.assertEquals(4, steps.size());
        Assertions.assertEquals("stepConstructionListeEtab", step1.getStepName());
        Assertions.assertEquals("ALLTYPEETAB", step1.getExitStatus().getExitCode());
        Assertions.assertEquals("stepTraiterEtabSansIp", step2.getStepName());
        Assertions.assertEquals("COMPLETED", step2.getExitStatus().getExitCode());
        Assertions.assertEquals("stepTraiterSuppressionIp", step3.getStepName());
        Assertions.assertEquals("COMPLETED", step3.getExitStatus().getExitCode());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
        Assertions.assertEquals("stepEnvoiMailRelance", step4.getStepName());
        Assertions.assertEquals(1, ((List<EtablissementDto>) jobExecution.getExecutionContext().get("etablissementDtos")).size());
        Assertions.assertEquals("COMPLETED", step3.getExitStatus().getExitCode());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
    }

}
