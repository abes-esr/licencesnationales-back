package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.event.etablissement.EtablissementSupprimeEvent;
import fr.abes.licencesnationales.repository.ContactRepository;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementSupprimeListener implements ApplicationListener<EtablissementSupprimeEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementSupprimeListener(EtablissementRepository etablissementRepository, ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        etablissementRepository.deleteBySiren(etablissementSupprimeEvent.getSiren());
    }
}
