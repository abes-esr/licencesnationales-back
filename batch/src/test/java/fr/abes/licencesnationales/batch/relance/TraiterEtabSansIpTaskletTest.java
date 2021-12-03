package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EventService;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TraiterEtabSansIpTasklet.class)
public class TraiterEtabSansIpTaskletTest {

    @InjectMocks
    private TraiterEtabSansIpTasklet tasklet;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EventService eventService;

    @Test
    void testTaskletTraiteEtabSansIp() throws Exception {
        Mockito.doNothing().when(emailService).constructRelanceEtabMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(eventService.getLastDateSuppressionIpEtab(Mockito.any())).thenReturn(null);
        Mockito.when(eventService.getDateCreationEtab(Mockito.any())).thenReturn(Calendar.getInstance().getTime());

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);
        List<EtablissementEntity> etabSansIp = new ArrayList<>();
        etabSansIp.add(etabIn1);
        stepExecution.getExecutionContext().put("etabSansIp", etabSansIp);

        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        tasklet = new TraiterEtabSansIpTasklet(emailService, eventService);
        tasklet.beforeStep(stepExecution);
        RepeatStatus status = tasklet.execute(stepContribution, chunkContext);
        Assertions.assertEquals(RepeatStatus.FINISHED, status);
    }
}
