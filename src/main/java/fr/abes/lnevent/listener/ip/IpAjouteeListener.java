package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpAjouteeEvent> {

    private final EtablissementRepository etablissementRepository;

    public IpAjouteeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(IpAjouteeEvent ipAjouteeEvent) {
        IpEntity ipEntity = new IpEntity(null,
                ipAjouteeEvent.getIp());
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(ipAjouteeEvent.getSiren());
        etablissementEntity.getIps().add(ipEntity);

        etablissementRepository.save(etablissementEntity);
    }
}
