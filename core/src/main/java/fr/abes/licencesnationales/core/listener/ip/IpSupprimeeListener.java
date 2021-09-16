package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpSupprimeeListener implements ApplicationListener<IpSupprimeeEventEntity> {

    private final EtablissementService service;

    @Autowired
    private IpRepository ipRepository;


    public IpSupprimeeListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(IpSupprimeeEventEntity ipSupprimeeEvent) {
        log.info("ipSupprimeeEvent.getSiren() = " + ipSupprimeeEvent.getSiren());
        log.info("ipSupprimeeEvent.getId() = " + ipSupprimeeEvent.getId());
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipSupprimeeEvent.getSiren());
        IpEntity ipEntity = ipRepository.getFirstById(ipSupprimeeEvent.getId());
        log.info("etablissementEntity.getIps().remove = ");
        etablissementEntity.getIps().remove(ipEntity);
        service.save(etablissementEntity);
    }
}
