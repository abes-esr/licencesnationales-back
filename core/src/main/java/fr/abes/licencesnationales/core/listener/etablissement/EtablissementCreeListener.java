package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEventEntity> {
    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public EtablissementCreeListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(EtablissementCreeEventEntity etablissementCreeEvent) {
        EtablissementEntity etablissementEntity = utilsMapper.map(etablissementCreeEvent, EtablissementEntity.class);
        service.save(etablissementEntity);


    }
}
