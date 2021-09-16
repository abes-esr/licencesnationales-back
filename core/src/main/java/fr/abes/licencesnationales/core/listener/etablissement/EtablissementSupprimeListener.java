package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementSupprimeEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementSupprimeListener implements ApplicationListener<EtablissementSupprimeEventEntity> {

    private final EtablissementService etablissementService;

    public EtablissementSupprimeListener(EtablissementService etablissementService) {
        this.etablissementService = etablissementService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementSupprimeEventEntity etablissementSupprimeEvent) {
        etablissementService.deleteBySiren(etablissementSupprimeEvent.getSiren());
    }
}
