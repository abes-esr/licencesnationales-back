package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpValideeListener implements ApplicationListener<IpValideeEvent> {

    private final EtablissementRepository etablissementRepository;

    public IpValideeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(IpValideeEvent ipValideeEvent) {
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(ipValideeEvent.getSiren());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getIp().equals(ipValideeEvent.getIp()))
                .findFirst().get().setValidee(true);

        etablissementRepository.save(etablissementEntity);
    }
}
