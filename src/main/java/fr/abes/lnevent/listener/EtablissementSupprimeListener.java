package fr.abes.lnevent.listener;

import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.EtablissementSupprimeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementSupprimeListener implements ApplicationListener<EtablissementSupprimeEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementSupprimeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        etablissementRepository.deleteBySiren(etablissementSupprimeEvent.getSiren());
    }
}
