package fr.abes.lnevent.listener;

import fr.abes.lnevent.entities.EtablissementRow;
import fr.abes.lnevent.repository.EtablissementRepository;
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
        etablissementRepository.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        for (String etablissementDivise :
                etablissementDiviseEvent.getSirens()) {
            etablissementRepository.save(new EtablissementRow(null,
                    etablissementDivise,
                    null,
                    etablissementDivise,
                    null,
                    null,
                    null));
        }
    }
}
