package fr.abes.licencesnationales.listener.ip;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
