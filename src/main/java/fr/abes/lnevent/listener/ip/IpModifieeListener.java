package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.entities.IpEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpModifieeListener implements ApplicationListener<IpModifieeEvent> {

    private final EtablissementRepository etablissementRepository;

    public IpModifieeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(IpModifieeEvent ipModifieeEvent) {
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(ipModifieeEvent.getSiren());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getIp().equals(ipModifieeEvent.getIp()))
                .findFirst().get().setIp(ipModifieeEvent.getIp());

        etablissementRepository.save(etablissementEntity);
    }
}
