package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.repository.entities.IpRow;
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
        IpRow ipRow = new IpRow(ipValideeEvent.getId(),
                ipValideeEvent.getIp(),
                ipValideeEvent.getSiren());
        ipRepository.save(ipRow);
    }
}
