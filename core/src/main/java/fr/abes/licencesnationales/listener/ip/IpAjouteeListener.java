package fr.abes.licencesnationales.listener.ip;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpAjouteeEvent> {

    private final EtablissementService service;

    public IpAjouteeListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(IpAjouteeEvent ipAjouteeEvent) {
        IpEntity ipEntity = new IpEntity(null,
                ipAjouteeEvent.getIp(), ipAjouteeEvent.getTypeAcces(), ipAjouteeEvent.getTypeIp(), ipAjouteeEvent.getCommentaires());
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipAjouteeEvent.getSiren());
        etablissementEntity.getIps().add(ipEntity);

        service.save(etablissementEntity);
    }
}
