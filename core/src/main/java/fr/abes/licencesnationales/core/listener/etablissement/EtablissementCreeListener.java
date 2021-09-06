package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEvent> {

    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public EtablissementCreeListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(EtablissementCreeEvent etablissementCreeEvent) {
        EtablissementEntity etablissementEntity = utilsMapper.map(etablissementCreeEvent, EtablissementEntity.class);
        service.save(etablissementEntity);

    }
}
