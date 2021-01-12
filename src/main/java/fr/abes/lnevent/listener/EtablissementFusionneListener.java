package fr.abes.lnevent.listener;

import fr.abes.lnevent.entities.EtablissementRow;
import fr.abes.lnevent.repository.EtablissementRepository;
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

        etablissementRepository.save(new EtablissementRow(null,
                null,
                null,
                etablissementFusionneEvent.getSiren(),
                null,
                null,
                null));

        for (String etablissementFusionne :
                etablissementFusionneEvent.getSirenFusionne()) {
            etablissementRepository.deleteBySiren(etablissementFusionne);
        }
    }
}
