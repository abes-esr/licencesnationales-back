package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.batch.JobRelanceConfigurationTest;
import fr.abes.licencesnationales.batch.JobRelancesConfig;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
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
import java.util.List;

@SpringBootTest(classes = {JobRelancesConfig.class, JobRelanceConfigurationTest.class})
@EnableAutoConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {JobRelancesConfig.class})
public class ConstructionListeEtabTaskletTest {
    @Autowired
    private EtablissementService etablissementService;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    void cleanUp() {
        this.jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @DisplayName("test avec uniquement etab sans IP")
    void testTaskletEtabSansIp() {

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom", "000000000", type, "12345", contact2);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        Mockito.when(etablissementService.findAll()).thenReturn(listIn);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("stepContructionListeEtab");

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("ETABSANSIPONLY", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(2, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).size());
    }

    @Test
    @DisplayName("Test avec etab avec IP en statut attestation demandée")
    void testTaskletEtabAvecIpAttestation() throws IpException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "000000000", type, "12345", contact2);

        StatutIpEntity statutAttestation = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        StatutIpEntity statutNouvelleIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "Nouvelle IP");

        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statutAttestation);
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test", statutNouvelleIp);
        IpEntity ip3 = new IpV4(3, "3.3.3.3", "test", statutNouvelleIp);

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn2.ajouterIp(ip3);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        Mockito.when(etablissementService.findAll()).thenReturn(listIn);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("stepContructionListeEtab");

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("ETABAVECAUMOINSUNEIPATTESTATIONONLY", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
        Assertions.assertEquals("testNom", ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).get(0).getNom());
    }

    @Test
    @DisplayName("Test avec etab avec IP en statut attestation demandée + etab sans IP")
    void testTaskletEtabAvecIpAttestationEtEtabSansIp() throws IpException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        StatutIpEntity statutAttestation = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        StatutIpEntity statutNouvelleIp = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "Nouvelle IP");

        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "000000000", type, "12345", contact1);
        EtablissementEntity etabIn3 = new EtablissementEntity(2, "testNom3", "000000000", type, "12345", contact1);

        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statutAttestation);
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test", statutNouvelleIp);
        IpEntity ip3 = new IpV4(3, "3.3.3.3", "test", statutNouvelleIp);

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn2.ajouterIp(ip3);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);
        listIn.add(etabIn3);

        Mockito.when(etablissementService.findAll()).thenReturn(listIn);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("stepContructionListeEtab");

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("ALLTYPEETAB", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).size());
        Assertions.assertEquals("testNom3", ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).get(0).getNom());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
        Assertions.assertEquals("testNom", ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).get(0).getNom());
    }
}
