package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.batch.relance.dto.EtablissementDto;
import fr.abes.licencesnationales.core.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
public class EnvoiMailRelanceTasklet implements Tasklet, StepExecutionListener {
    private List<EtablissementDto> etablissementDtos;

    private EmailService emailService;

    @Value("${ln.dest.notif.admin}")
    private String mailAdmin;

    public EnvoiMailRelanceTasklet(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.etablissementDtos = (List<EtablissementDto>) stepExecution.getJobExecution().getExecutionContext().get("etablissementDtos");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (EtablissementDto dto : etablissementDtos) {
            try {
                    emailService.constructSuppresionIpMail(dto.getIpsSupprimees(), dto.getIpsAttestation(), dto.getEmail(), mailAdmin);
            } catch (RestClientException ex) {
                log.error("JOB Suppression IP : Erreur dans l'envoi du mail pour l'établissement " + dto.getNomEtab());
            }
        }
        return RepeatStatus.FINISHED;
    }
}
