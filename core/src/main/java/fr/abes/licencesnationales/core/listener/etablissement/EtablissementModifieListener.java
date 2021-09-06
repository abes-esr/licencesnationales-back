package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.ContactEntity;
import fr.abes.licencesnationales.core.entities.EtablissementEntity;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEvent> {

    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public EtablissementModifieListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementModifieEvent etablissementModifieEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(etablissementModifieEvent.getSiren());
        etablissementEntity.setContact(utilsMapper.map(etablissementModifieEvent, ContactEntity.class));
        service.save(etablissementEntity);
    }
}
