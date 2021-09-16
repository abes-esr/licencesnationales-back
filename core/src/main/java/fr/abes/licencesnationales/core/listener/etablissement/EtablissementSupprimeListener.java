package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementSupprimeEventEntity;
import fr.abes.licencesnationales.core.services.ContactService;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementSupprimeListener implements ApplicationListener<EtablissementSupprimeEventEntity> {

    private final EtablissementService etablissementService;
    private final ContactService contactService;

    public EtablissementSupprimeListener(EtablissementService etablissementService, ContactService contactService) {
        this.etablissementService = etablissementService;
        this.contactService = contactService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementSupprimeEventEntity etablissementSupprimeEvent) {
        etablissementService.deleteBySiren(etablissementSupprimeEvent.getSiren());
    }
}
