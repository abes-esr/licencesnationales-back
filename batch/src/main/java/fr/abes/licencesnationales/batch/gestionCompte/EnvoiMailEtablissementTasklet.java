package fr.abes.licencesnationales.batch.gestionCompte;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EmailService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EnvoiMailEtablissementTasklet implements Tasklet, StepExecutionListener {
    @Autowired
    private EmailService service;

    private List<EtablissementEntity> listeEtab;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.listeEtab = (List<EtablissementEntity>) stepExecution.getJobExecution().getExecutionContext().get("listeEtab");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        return null;
    }
}
