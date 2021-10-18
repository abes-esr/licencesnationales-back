package fr.abes.licencesnationales.batch.SuppressionIpValidation.traiterSuppressionIpChunk;

import fr.abes.licencesnationales.batch.SuppressionIpValidation.IpDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class IpSuppressionReader implements ItemReader<IpDto>, StepExecutionListener {
    private List<IpDto> ipList;
    AtomicInteger i = new AtomicInteger();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.ipList = (List<IpDto>) executionContext.get("ipList");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public IpDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("Entrée dans le reader");
        IpDto ip = null;
        if (i.intValue() < this.ipList.size()) {
            ip = this.ipList.get(i.getAndIncrement());
        }
        return ip;
    }
}
