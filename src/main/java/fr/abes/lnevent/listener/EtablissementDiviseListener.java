package fr.abes.lnevent.listener;

import fr.abes.lnevent.collection.EtablissementCollection;
import fr.abes.lnevent.collection.repository.EtablissementRepository;
import fr.abes.lnevent.event.EtablissementDiviseEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementDiviseListener implements ApplicationListener<EtablissementDiviseEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementDiviseListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementDiviseEvent etablissementDiviseEvent) {
        etablissementRepository.deleteByName(etablissementDiviseEvent.getVieuxNom());
        for (String etablissementDivise :
                etablissementDiviseEvent.getEtablisementsDivise()) {
            etablissementRepository.save(new EtablissementCollection(null, etablissementDivise));
        }
    }
}
