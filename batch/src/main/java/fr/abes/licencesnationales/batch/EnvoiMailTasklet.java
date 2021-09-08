package fr.abes.licencesnationales.batch;

import fr.abes.licencesnationales.core.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class EnvoiMailTasklet implements Tasklet, InitializingBean {
    @Autowired
    private EmailService emailService;
    @Value("${ln.dest.notif.admin}")
    private String mailAdmin;
    @Value("${ln.skipMail}")
    private boolean skipMail;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String ip = chunkContext.getStepContext().getStepExecutionContext().get("ip").toString();
        String nomEtab = chunkContext.getStepContext().getStepExecutionContext().get("nomEtab").toString();
        String mailEtab = chunkContext.getStepContext().getStepExecutionContext().get("mailEtab").toString();
        try {
            if (skipMail) {
                emailService.constructSuppresionIpMail(ip, nomEtab, mailAdmin, null);
            } else {
                emailService.constructSuppresionIpMail(ip, nomEtab, mailEtab, mailAdmin);
            }
        } catch (RestClientException ex) {
            log.error("JOB Suppression IP : Erreur dans l'envoi du mail pour l'IP " + ip + " et l'établissement " + nomEtab);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
