package fr.abes.licencesnationales.listener.ip;


import fr.abes.licencesnationales.converter.UtilsMapper;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.services.EtablissementService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpModifieeListener implements ApplicationListener<IpModifieeEvent> {

    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public IpModifieeListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.utilsMapper = utilsMapper;
        this.service = service;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(IpModifieeEvent ipModifieeEvent) {
        log.info("ipModifieeEvent.getSiren()= " + ipModifieeEvent.getSiren());
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipModifieeEvent.getSiren());
        etablissementEntity.getIps().removeIf(entity -> entity.getId().equals(ipModifieeEvent.getId()));
        etablissementEntity.getIps().add(utilsMapper.map(ipModifieeEvent, IpEntity.class));
        service.save(etablissementEntity);
    }
}
