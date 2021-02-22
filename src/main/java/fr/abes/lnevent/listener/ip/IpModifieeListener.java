package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpModifieeListener implements ApplicationListener<IpModifieeEvent> {

    private final IpRepository ipRepository;

    public IpModifieeListener(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    @Override
    public void onApplicationEvent(IpModifieeEvent ipModifieeEvent) {
        IpEntity ipEntity = new IpEntity(ipModifieeEvent.getId(),
                ipModifieeEvent.getIp(),
                ipModifieeEvent.getSiren());
        ipRepository.save(ipEntity);
    }
}
