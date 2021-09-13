package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpValideeListener implements ApplicationListener<IpValideeEvent> {
    private final StatutRepository statutRepository;
    private final EtablissementService service;

    public IpValideeListener(StatutRepository statutRepository, EtablissementService service) {
        this.statutRepository = statutRepository;
        this.service = service;
    }

    @Override
    public void onApplicationEvent(IpValideeEvent ipValideeEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipValideeEvent.getSiren());
        etablissementEntity.getIps().stream().filter(ipEntity -> ipEntity.getIp().equals(ipValideeEvent.getIp()))
                .findFirst().get().setStatut((StatutIpEntity) statutRepository.findById(Constant.STATUT_IP_VALIDEE).get());

        service.save(etablissementEntity);
    }

}
