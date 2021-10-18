package fr.abes.licencesnationales.batch.SuppressionIpValidation.traiterSuppressionIpChunk;

import fr.abes.licencesnationales.batch.SuppressionIpValidation.IpDto;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import fr.abes.licencesnationales.core.services.EmailService;
import fr.abes.licencesnationales.core.services.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IpSuppressionWriter implements ItemWriter<IpSupprimeeEventEntity>, StepExecutionListener {

    private List<IpDto> ipList;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IpEventRepository eventRepository;

    @Autowired
    private EmailService emailService;

    @Value("${ln.skipMail}")
    private boolean skipMail;

    @Value("${ln.dest.notif.admin}")
    private String mailAdmin;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.ipList = (List<IpDto>) executionContext.get("ipList");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

    @Override
    public void write(List<? extends IpSupprimeeEventEntity> list) throws Exception {
        list.stream().forEach(ipSupprimeeEvent -> {
            applicationEventPublisher.publishEvent(ipSupprimeeEvent);
            eventRepository.save(ipSupprimeeEvent);
            log.info("IP : " + ipSupprimeeEvent.getIp());

            IpDto ip = this.ipList.stream().filter(i -> i.getId().equals(ipSupprimeeEvent.getIpId())).collect(Collectors.toList()).get(0);
            try {
                if (skipMail) {
                    emailService.constructSuppresionIpMail(ip.getIp(), ip.getNomEtab(), mailAdmin, null);
                } else {
                    emailService.constructSuppresionIpMail(ip.getIp(), ip.getNomEtab(), ip.getMail(), mailAdmin);
                }
            } catch (RestClientException ex) {
                log.error("JOB Suppression IP : Erreur dans l'envoi du mail pour l'IP " + ip.getIp() + " et l'établissement " + ip.getNomEtab());
            }
        });
    }
}
