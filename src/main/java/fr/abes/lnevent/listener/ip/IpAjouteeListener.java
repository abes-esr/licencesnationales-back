package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.entities.IpRow;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpAjouteeEvent> {

    private final IpRepository ipRepository;

    public IpAjouteeListener(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    @Override
    public void onApplicationEvent(IpAjouteeEvent ipAjouteeEvent) {
        IpRow ipRow = new IpRow(null,
                ipAjouteeEvent.getIp(),
                ipAjouteeEvent.getSiren());
        ipRepository.save(ipRow);
    }
}
