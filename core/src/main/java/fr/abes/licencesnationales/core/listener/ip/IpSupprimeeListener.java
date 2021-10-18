package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.entities.ip.event.IpSupprimeeEventEntity;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpSupprimeeListener implements ApplicationListener<IpSupprimeeEventEntity> {

    private final IpService service;


    public IpSupprimeeListener(IpService service) {
        this.service = service;
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(IpSupprimeeEventEntity ipSupprimeeEvent) {
        service.deleteById(ipSupprimeeEvent.getIpId());
    }
}
