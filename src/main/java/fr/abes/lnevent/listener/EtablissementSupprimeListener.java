package fr.abes.lnevent.listener;

import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.EtablissementSupprimeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementSupprimeListener implements ApplicationListener<EtablissementSupprimeEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementSupprimeListener(EtablissementRepository etablissementRepository, ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        etablissementRepository.deleteBySiren(etablissementSupprimeEvent.getSiren());
        contactRepository.deleteBySiren(etablissementSupprimeEvent.getSiren());
    }
}
