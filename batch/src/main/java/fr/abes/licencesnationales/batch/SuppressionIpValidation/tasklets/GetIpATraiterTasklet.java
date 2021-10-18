package fr.abes.licencesnationales.batch.SuppressionIpValidation.tasklets;

import fr.abes.licencesnationales.batch.SuppressionIpValidation.IpDto;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.services.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GetIpATraiterTasklet implements Tasklet, StepExecutionListener {
    private List<IpDto> ipList;

    private final IpService service;

    public GetIpATraiterTasklet(IpService ipService) {
        this.ipList = new ArrayList<>();
        this.service = ipService;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
            stepExecution.getJobExecution().getExecutionContext().put("ipList", this.ipList);
        }
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("Récupération des IP à traiter");
        try {
            for (IpEntity ip : service.getIpValidationOlderThanOneYear()) {
                this.ipList.add(new IpDto(ip));
            }
        } catch (DataAccessException e) {
            log.error("Erreur SQL : " + e.getMessage());
            stepContribution.setExitStatus(ExitStatus.FAILED);
        }
        return RepeatStatus.FINISHED;
    }
}
