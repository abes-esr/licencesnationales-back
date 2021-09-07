package fr.abes.licencesnationales.traiterSuppressionIpChunk;

import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpSupprimeeEvent;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class IpSuppressionProcessor implements ItemProcessor<IpEntity, IpSupprimeeEvent> {
    @Override
    public IpSupprimeeEvent process(IpEntity entity) throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        IpSupprimeeEvent ipSupprimeeEvent = new IpSupprimeeEvent(this, entity.getId(), entity.getEtablissement().getSiren());
        executionContext.putString("mailEtab", entity.getEtablissement().getContact().getMail());
        executionContext.putString("nomEtab", entity.getEtablissement().getName());
        return ipSupprimeeEvent;
    }
}
