package fr.abes.lnevent.listener;

import fr.abes.lnevent.collection.EtablissementCollection;
import fr.abes.lnevent.collection.repository.EtablissementRepository;
import fr.abes.lnevent.event.EtablissementCreeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementCreeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementCreeEvent etablissementCreeEvent) {
        etablissementRepository.save(new EtablissementCollection(null, etablissementCreeEvent.getNom()));
    }
}
