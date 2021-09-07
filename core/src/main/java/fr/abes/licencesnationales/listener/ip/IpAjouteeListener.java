package fr.abes.licencesnationales.listener.ip;


import fr.abes.licencesnationales.converter.UtilsMapper;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpAjouteeEvent> {

    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public IpAjouteeListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(IpAjouteeEvent ipAjouteeEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipAjouteeEvent.getSiren());
        etablissementEntity.getIps().add(utilsMapper.map(ipAjouteeEvent, IpEntity.class));

        service.save(etablissementEntity);
    }
}
