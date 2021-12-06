package fr.abes.licencesnationales.core.listener.etablissement;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementValideEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementValideListener implements ApplicationListener<EtablissementValideEventEntity> {
    private final EtablissementService service;

    public EtablissementValideListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void onApplicationEvent(EtablissementValideEventEntity event) {
        EtablissementEntity etab = service.getFirstBySiren(event.getSiren());
        etab.setValide(true);
        service.save(etab);
    }
}
