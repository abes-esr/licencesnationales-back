package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.repository.IpRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpSupprimeeListener implements ApplicationListener<IpSupprimeeEvent> {

    private final IpRepository ipRepository;

    public IpSupprimeeListener(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    @Override
    public void onApplicationEvent(IpSupprimeeEvent ipSupprimeeEvent) {
        ipRepository.deleteByIpAndSiren(ipSupprimeeEvent.getIp(),
                ipSupprimeeEvent.getSiren());
    }
}
