package fr.abes.licencesnationales.batch.relance;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JobRelanceConfigurationTest.class, JobRelancesConfig.class}, properties = "spring.batch.job.enabled=false")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConstructionListeEtabTaskletTest {
    @Autowired
    private JobLauncherTestUtils launcherGestionCompte;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private EtablissementService service;

    @Test
    @DisplayName("test avec uniquement etab sans IP")
    void testTaskletEtabSansIp() {
        this.jobRepositoryTestUtils.removeJobExecutions();

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom", "000000000", type, "12345", contact2);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        Mockito.when(service.findAll()).thenReturn(listIn);

        JobExecution jobExecution = launcherGestionCompte.launchStep("stepContructionListeEtab");

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(2, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).size());
        Assertions.assertEquals(0, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
    }

    @Test
    @DisplayName("Test avec etab avec IP en statut attestation demandée")
    void testTaskletEtabAvecIpAttestation() throws IpException {
        this.jobRepositoryTestUtils.removeJobExecutions();

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

        Mockito.when(service.findAll()).thenReturn(listIn);

        JobExecution jobExecution = launcherGestionCompte.launchStep("stepContructionListeEtab");

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(0, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).size());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
        Assertions.assertEquals("testNom", ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).get(0).getNom());
    }

    @Test
    @DisplayName("Test avec etab avec IP en statut attestation demandée + etab sans IP")
    void testTaskletEtabAvecIpAttestationEtEtabSansIp() throws IpException {
        this.jobRepositoryTestUtils.removeJobExecutions();

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

        Mockito.when(service.findAll()).thenReturn(listIn);

        JobExecution jobExecution = launcherGestionCompte.launchStep("stepContructionListeEtab");

        Assertions.assertEquals(1, jobExecution.getStepExecutions().size());
        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).size());
        Assertions.assertEquals("testNom3", ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabSansIp")).get(0).getNom());
        Assertions.assertEquals(1, ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).size());
        Assertions.assertEquals("testNom", ((List<EtablissementEntity>) jobExecution.getExecutionContext().get("etabAvecAuMoinsUneIpAttestation")).get(0).getNom());
    }
}
