package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.converter.UtilsMapper;
import fr.abes.licencesnationales.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.services.EtablissementService;
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
