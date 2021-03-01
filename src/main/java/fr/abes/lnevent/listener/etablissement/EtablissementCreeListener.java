package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
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
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissementCreeEvent.getNomContact(),
                        etablissementCreeEvent.getPrenomContact(),
                        etablissementCreeEvent.getMailContact(),
                        etablissementCreeEvent.getTelephoneContact(),
                        etablissementCreeEvent.getAdresseContact());
        EtablissementEntity etablissementEntity =
                new EtablissementEntity(null,
                        etablissementCreeEvent.getNom(),
                        etablissementCreeEvent.getAdresse(),
                        etablissementCreeEvent.getSiren(),
                        etablissementCreeEvent.getMotDePasse(),
                        etablissementCreeEvent.getTypeEtablissement(),
                        etablissementCreeEvent.getIdAbes(),
                        contactEntity,
                        null);

        etablissementRepository.save(etablissementEntity);

    }
}
