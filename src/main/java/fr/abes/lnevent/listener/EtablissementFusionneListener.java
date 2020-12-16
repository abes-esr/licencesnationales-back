package fr.abes.lnevent.listener;

import fr.abes.lnevent.collection.EtablissementCollection;
import fr.abes.lnevent.collection.repository.EtablissementRepository;
import fr.abes.lnevent.event.EtablissementFusionneEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementFusionneListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

        etablissementRepository.save(new EtablissementCollection(null, etablissementFusionneEvent.getNom()));

        for (String etablissementFusionne :
                etablissementFusionneEvent.getEtablissementsFusionne()) {
            etablissementRepository.deleteByName(etablissementFusionne);
        }
    }
}
