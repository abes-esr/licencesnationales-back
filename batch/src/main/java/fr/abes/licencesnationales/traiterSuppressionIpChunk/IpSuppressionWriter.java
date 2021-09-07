package fr.abes.licencesnationales.traiterSuppressionIpChunk;

import fr.abes.licencesnationales.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.services.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IpSuppressionWriter extends AbstractItemStreamItemWriter<IpSupprimeeEvent>
        implements InitializingBean, ItemWriter<IpSupprimeeEvent> {

    private final IpService ipService;

    public IpSuppressionWriter(IpService ipService) {
        this.ipService = ipService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void write(List<? extends IpSupprimeeEvent> list) throws Exception {
        list.stream().forEach(ipSupprimeeEvent -> { log.debug("IP : " + ipSupprimeeEvent.getId());});
    }
}
