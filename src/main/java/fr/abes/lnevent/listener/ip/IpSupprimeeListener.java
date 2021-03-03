package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.repository.EtablissementRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpSupprimeeListener implements ApplicationListener<IpSupprimeeEvent> {

    private final EtablissementRepository etablissementRepository;

    public IpSupprimeeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(IpSupprimeeEvent ipSupprimeeEvent) {
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(ipSupprimeeEvent.getSiren());
        etablissementEntity.getIps().removeIf(ipEntity -> ipEntity.getIp().equals(ipSupprimeeEvent.getIp()));

        etablissementRepository.save(etablissementEntity);
    }
}
