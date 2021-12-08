package fr.abes.licencesnationales.batch.gestionCompte;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SuppressionEtEnvoiMailTasklet implements Tasklet, StepExecutionListener {
    private EtablissementService etablissementService;
    private EmailService emailService;

    private List<EtablissementEntity> listeEtab;

    public SuppressionEtEnvoiMailTasklet(EtablissementService etablissementService, EmailService emailService) {
        this.etablissementService = etablissementService;
        this.emailService = emailService;
        this.listeEtab = new ArrayList<>();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.listeEtab = (List<EtablissementEntity>) stepExecution.getJobExecution().getExecutionContext().get("listeEtab");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (EtablissementEntity etab : this.listeEtab) {
            try {
                etablissementService.deleteBySiren(etab.getSiren());
                String motif = "Suppression automatique après un an d'inactivité";
                emailService.constructSuppressionMail(motif, etab.getNom(), etab.getContact().getMail());
            } catch (Exception ex) {
                log.error("Erreur dans la suppression de l'établissement. Siren : " + etab.getSiren() + " / cause : " + ex.getMessage());
            }
        }
        return RepeatStatus.FINISHED;
    }
}
