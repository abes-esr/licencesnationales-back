package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpValideeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpValideeListener implements ApplicationListener<IpValideeEventEntity> {
    private final ReferenceService referenceService;
    private final IpService service;

    public IpValideeListener(ReferenceService referenceService, IpService service) {
        this.referenceService = referenceService;
        this.service = service;
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(IpValideeEventEntity ipValideeEvent) {
        IpEntity ipEntity = service.getFirstById(ipValideeEvent.getId());
        ipEntity.setStatut((StatutIpEntity) referenceService.findStatutById(Constant.STATUT_IP_VALIDEE));
        service.save(ipEntity);
    }

}
