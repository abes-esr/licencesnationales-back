package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpValideeListener implements ApplicationListener<IpValideeEvent> {

    private final EtablissementService service;

    public IpValideeListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(IpValideeEvent ipValideeEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipValideeEvent.getSiren());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getIp().equals(ipValideeEvent.getIp()))
                .findFirst().get().setValidee(true);

        service.save(etablissementEntity);
    }

}
