package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.event.etablissement.EtablissementModifieEvent;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.entities.ContactRow;
import fr.abes.lnevent.entities.EtablissementRow;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementModifieListener(EtablissementRepository etablissementRepository,
                                     ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementModifieEvent etablissementModifieEvent) {
        EtablissementRow etablissementRow =
                new EtablissementRow(
                        etablissementModifieEvent.getIdEtablissement(),
                        etablissementModifieEvent.getNom(),
                        etablissementModifieEvent.getAdresse(),
                        etablissementModifieEvent.getSiren(),
                        etablissementModifieEvent.getTypeEtablissement(),
                        etablissementModifieEvent.getIdAbes());

        etablissementRepository.save(etablissementRow);

        ContactRow contactRow =
                new ContactRow(null,
                        etablissementModifieEvent.getNomContact(),
                        etablissementModifieEvent.getPrenomContact(),
                        etablissementModifieEvent.getMailContact(),
                        etablissementModifieEvent.getMotDePasse(),
                        etablissementModifieEvent.getTelephoneContact(),
                        etablissementModifieEvent.getAdresseContact(),
                        etablissementModifieEvent.getSiren());

        contactRepository.save(contactRow);
    }
}
