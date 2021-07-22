package fr.abes.licencesnationales.listener.ip;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.repository.IpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpSupprimeeListener implements ApplicationListener<IpSupprimeeEvent> {

    private final EtablissementRepository etablissementRepository;

    @Autowired
    private IpRepository ipRepository;


    public IpSupprimeeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(IpSupprimeeEvent ipSupprimeeEvent) {
        log.info("ipSupprimeeEvent.getSiren() = " + ipSupprimeeEvent.getSiren());
        log.info("ipSupprimeeEvent.getId() = " + ipSupprimeeEvent.getId());
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(ipSupprimeeEvent.getSiren());
        IpEntity ipEntity = ipRepository.getFirstById(Long.parseLong(ipSupprimeeEvent.getId()));
        log.info("etablissementEntity.getIps().remove = ");
        etablissementEntity.getIps().remove(ipEntity);
        etablissementRepository.save(etablissementEntity);
    }
}
