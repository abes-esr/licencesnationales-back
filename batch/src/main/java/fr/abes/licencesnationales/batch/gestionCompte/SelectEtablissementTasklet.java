package fr.abes.licencesnationales.batch.gestionCompte;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
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
public class SelectEtablissementTasklet implements Tasklet, StepExecutionListener {

    private EtablissementService service;

    private List<EtablissementEntity> listeEtab;

    public SelectEtablissementTasklet(EtablissementService service) {
        this.service = service;
        this.listeEtab = new ArrayList<>();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            if (this.listeEtab.size() != 0) {
                stepExecution.getJobExecution().getExecutionContext().put("listeEtab", this.listeEtab);
                return ExitStatus.COMPLETED;
            }
            return new ExitStatus("AUCUNETAB");
        }
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            this.listeEtab = service.getEtabASupprimer();
        } catch (UnknownEtablissementException ex) {
            log.error(Constant.ERROR_ETAB_INCONNU + ex.getMessage());
        }
        return RepeatStatus.FINISHED;
    }
}
