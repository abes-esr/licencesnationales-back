package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementDiviseListener implements ApplicationListener<EtablissementDiviseEventEntity> {

    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public EtablissementDiviseListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementDiviseEventEntity etablissementDiviseEvent) {
        /*service.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        List<EtablissementEntity> etablissementEntities = utilsMapper.mapList(etablissementDiviseEvent.getEtablissements(), EtablissementEntity.class);
        service.saveAll(etablissementEntities);*/
    }
}
