package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ConstructionListeEtabTasklet implements Tasklet, StepExecutionListener {
    private List<EtablissementEntity> etabAvecAuMoinsUneIpAttestation;
    private List<EtablissementEntity> etabSansIp;

    private final EtablissementService service;

    public ConstructionListeEtabTasklet(EtablissementService service) {
        this.etabAvecAuMoinsUneIpAttestation = new ArrayList<>();
        this.etabSansIp = new ArrayList<>();
        this.service = service;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            if (this.etabSansIp.size() != 0 && this.etabAvecAuMoinsUneIpAttestation.size() != 0) {
                stepExecution.getJobExecution().getExecutionContext().put("etabSansIp", this.etabSansIp);
                stepExecution.getJobExecution().getExecutionContext().put("etabAvecAuMoinsUneIpAttestation", this.etabAvecAuMoinsUneIpAttestation);
                return new ExitStatus("ALLTYPEETAB");
            }
            if (this.etabSansIp.size() != 0) {
                stepExecution.getJobExecution().getExecutionContext().put("etabSansIp", this.etabSansIp);
                return new ExitStatus("ETABSANSIPONLY");
            }
            if (this.etabAvecAuMoinsUneIpAttestation.size() != 0) {
                stepExecution.getJobExecution().getExecutionContext().put("etabAvecAuMoinsUneIpAttestation", this.etabAvecAuMoinsUneIpAttestation);
                return new ExitStatus("ETABAVECAUMOINSUNEIPATTESTATIONONLY");
            }
        }
        return new ExitStatus("AUCUNETAB");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            List<EtablissementEntity> etab = service.findAll();
            this.etabSansIp = etab.stream().filter(e -> e.getIps().size() == 0).filter(e -> !e.getContact().getRole().equals("admin")).collect(Collectors.toList());
            this.etabAvecAuMoinsUneIpAttestation = etab.stream().filter(e ->
                e.getIps().stream().anyMatch(i -> i.getStatut().getIdStatut() == Constant.STATUT_IP_ATTESTATION)
            ).collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Erreur SQL : " + e.getMessage());
            stepContribution.setExitStatus(ExitStatus.FAILED);
        }
        return RepeatStatus.FINISHED;
    }
}
