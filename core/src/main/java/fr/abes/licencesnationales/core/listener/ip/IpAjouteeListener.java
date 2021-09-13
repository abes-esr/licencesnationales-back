package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.services.EtablissementService;
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
