package fr.abes.licencesnationales.listener.ip;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
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
