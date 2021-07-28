package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.event.etablissement.EtablissementSupprimeEvent;
import fr.abes.licencesnationales.services.ContactService;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementSupprimeListener implements ApplicationListener<EtablissementSupprimeEvent> {

    private final EtablissementService etablissementService;
    private final ContactService contactService;

    public EtablissementSupprimeListener(EtablissementService etablissementService, ContactService contactService) {
        this.etablissementService = etablissementService;
        this.contactService = contactService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        etablissementService.deleteBySiren(etablissementSupprimeEvent.getSiren());
    }
}
