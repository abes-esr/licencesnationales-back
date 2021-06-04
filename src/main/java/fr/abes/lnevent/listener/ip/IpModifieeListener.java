package fr.abes.lnevent.listener.ip;

import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.IpRepository;
import fr.abes.lnevent.entities.IpEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class IpModifieeListener implements ApplicationListener<IpModifieeEvent> {

    private final EtablissementRepository etablissementRepository;

    public IpModifieeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(IpModifieeEvent ipModifieeEvent) {
        log.info("ipModifieeEvent.getSiren()= " + ipModifieeEvent.getSiren());
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(ipModifieeEvent.getSiren());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getId().equals(ipModifieeEvent.getId()))
                .findFirst().get().setIp(ipModifieeEvent.getIp());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getId().equals(ipModifieeEvent.getId()))
                .findFirst().get().setValidee(ipModifieeEvent.getValidee().equals("1")? true:false);
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getId().equals(ipModifieeEvent.getId()))
                .findFirst().get().setDateModification(new Date());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getId().equals(ipModifieeEvent.getId()))
                .findFirst().get().setTypeAcces(ipModifieeEvent.getTypeAcces());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getId().equals(ipModifieeEvent.getId()))
                .findFirst().get().setTypeIp(ipModifieeEvent.getTypeIp());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getId().equals(ipModifieeEvent.getId()))
                .findFirst().get().setCommentaires(ipModifieeEvent.getCommentaires());

        etablissementRepository.save(etablissementEntity);
    }
}
