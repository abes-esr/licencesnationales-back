package fr.abes.licencesnationales.batch.relance.tasklets;

import fr.abes.licencesnationales.batch.relance.EtablissementDto;
import fr.abes.licencesnationales.batch.relance.IpDto;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.IpService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class traiterSuppressionIpTasklet implements Tasklet, StepExecutionListener {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IpEventRepository eventRepository;

    private List<EtablissementEntity> etabAvecAuMoinsUneIpAttestation;
    private List<EtablissementDto> etablissementDtos;

    Calendar oneYearAgo;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.etabAvecAuMoinsUneIpAttestation = (List<EtablissementEntity>) stepExecution.getJobExecution().getExecutionContext().get("etabAvecAuMoinsUneIpAttestation");
        this.etablissementDtos = new ArrayList<>();
        oneYearAgo = Calendar.getInstance();
        oneYearAgo.add(Calendar.YEAR, -1);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("etablissementDtos", this.etablissementDtos);
        return stepExecution.getExitStatus();
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (EtablissementEntity etab : etabAvecAuMoinsUneIpAttestation) {
            EtablissementDto dto = new EtablissementDto(etab.getNom(), etab.getSiren(), etab.getContact().getMail());
            etab.getIps().stream().filter(i -> i.getStatut().getIdStatut() == Constant.STATUT_IP_ATTESTATION).forEach(ip -> {
                if (ip.getDateModification().before(oneYearAgo.getTime())) {
                    //l'IP est dans l'état attestation à envoyer depuis plus d'un an
                    IpSupprimeeEventEntity ipSupprimeeEvent = new IpSupprimeeEventEntity(this, ip.getId(), ip.getIp());
                    ipSupprimeeEvent.setSiren(etab.getSiren());
                    applicationEventPublisher.publishEvent(ipSupprimeeEvent);
                    eventRepository.save(ipSupprimeeEvent);
                    dto.ajouterIpSupprimee(ip.getIp());
                }
                else {
                    dto.ajouterIpAttestation(ip.getIp());
                }
            });
            etablissementDtos.add(dto);
        }
        return RepeatStatus.FINISHED;
    }
}
