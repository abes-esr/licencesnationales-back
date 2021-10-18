package fr.abes.licencesnationales.batch.SuppressionIpValidation.traiterSuppressionIpChunk;

import fr.abes.licencesnationales.batch.SuppressionIpValidation.IpDto;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IpSuppressionProcessor implements ItemProcessor<IpDto, IpSupprimeeEventEntity>, StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public IpSupprimeeEventEntity process(IpDto dto) throws Exception {
        log.info("Entrée dans le processor");
        ExecutionContext executionContext = new ExecutionContext();
        IpSupprimeeEventEntity ipSupprimeeEvent = new IpSupprimeeEventEntity(this, dto.getId(), dto.getIp());
        ipSupprimeeEvent.setSiren(dto.getSiren());
        executionContext.putString("mailEtab", dto.getMail());
        executionContext.putString("nomEtab", dto.getNomEtab());
        return ipSupprimeeEvent;
    }
}
