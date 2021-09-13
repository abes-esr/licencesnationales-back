package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpAjouteeEvent> {
    private final StatutRepository statutRepository;
    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public IpAjouteeListener(StatutRepository statutRepository, EtablissementService service, UtilsMapper utilsMapper) {
        this.statutRepository = statutRepository;
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(IpAjouteeEvent ipAjouteeEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipAjouteeEvent.getSiren());
        IpEntity ip = utilsMapper.map(ipAjouteeEvent, IpEntity.class);
        ip.setStatut((StatutIpEntity) statutRepository.findById(Constant.STATUT_IP_NOUVELLE).get());
        etablissementEntity.getIps().add(ip);
        service.save(etablissementEntity);
    }
}
