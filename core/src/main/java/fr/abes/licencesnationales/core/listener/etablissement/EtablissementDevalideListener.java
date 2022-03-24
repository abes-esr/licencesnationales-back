package fr.abes.licencesnationales.core.listener.etablissement;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDevalideEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementDevalideListener implements ApplicationListener<EtablissementDevalideEventEntity> {
    private final EtablissementService service;

    public EtablissementDevalideListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void onApplicationEvent(EtablissementDevalideEventEntity event) {
        EtablissementEntity etab = service.getFirstBySiren(event.getSiren());
        etab.setValide(false);
        service.save(etab);
    }
}
