package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.event.etablissement.EtablissementModifieEvent;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementModifieListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementModifieEvent etablissementModifieEvent) {
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissementModifieEvent.getNomContact(),
                        etablissementModifieEvent.getPrenomContact(),
                        etablissementModifieEvent.getMailContact(),
                        etablissementModifieEvent.getTelephoneContact(),
                        etablissementModifieEvent.getAdresseContact());
        EtablissementEntity etablissementEntity =
                new EtablissementEntity(
                        etablissementModifieEvent.getIdEtablissement(),
                        etablissementModifieEvent.getNom(),
                        etablissementModifieEvent.getAdresse(),
                        etablissementModifieEvent.getSiren(),
                        etablissementModifieEvent.getMotDePasse(),
                        etablissementModifieEvent.getTypeEtablissement(),
                        etablissementModifieEvent.getIdAbes(),
                        contactEntity,
                        null);

        etablissementRepository.save(etablissementEntity);
    }
}
