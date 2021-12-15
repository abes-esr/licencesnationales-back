package fr.abes.licencesnationales.core.listener.ip;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpRejeteeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class IpRejeteeListener implements ApplicationListener<IpRejeteeEventEntity> {
    private final ReferenceService referenceService;
    private final IpService service;

    public IpRejeteeListener(ReferenceService referenceService, IpService ipService) {
        this.referenceService = referenceService;
        this.service = ipService;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(IpRejeteeEventEntity ipRejeteeEvent) {
        IpEntity ipEntity = service.getFirstById(ipRejeteeEvent.getIpId());
        ipEntity.setDateModification(new Date());
        ipEntity.setStatut((StatutIpEntity) referenceService.findStatutById(Constant.STATUT_IP_ATTESTATION));
        service.save(ipEntity);
    }
}
