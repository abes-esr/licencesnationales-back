package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpValideeListener implements ApplicationListener<IpValideeEvent> {

    private final IpRepository ipRepository;

    public IpValideeListener(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    @Override
    public void onApplicationEvent(IpValideeEvent ipValideeEvent) {
        IpEntity ipEntity = new IpEntity(ipValideeEvent.getId(),
                ipValideeEvent.getIp(),
                ipValideeEvent.getSiren());
        ipRepository.save(ipEntity);
    }
}
