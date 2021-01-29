package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.event.editeur.dto.etablissement.Etablissement;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.entities.ContactRow;
import fr.abes.lnevent.repository.entities.EtablissementRow;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementFusionneEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementFusionneListener(EtablissementRepository etablissementRepository, ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

        Etablissement etablissementFusione = etablissementFusionneEvent.getEtablissement();

        etablissementRepository.save(new EtablissementRow(null,
                etablissementFusione.getNom(),
                etablissementFusione.getAdresse(),
                etablissementFusione.getSiren(),
                etablissementFusione.getTypeEtablissement(),
                etablissementFusione.getIdAbes()));

        ContactRow contactRow =
                new ContactRow(null,
                        etablissementFusione.getNomContact(),
                        etablissementFusione.getPrenomContact(),
                        etablissementFusione.getMailContact(),
                        etablissementFusione.getMotDePasse(),
                        etablissementFusione.getTelephoneContact(),
                        etablissementFusione.getAdresseContact(),
                        etablissementFusione.getSiren());

        contactRepository.save(contactRow);

        for (String siren :
                etablissementFusionneEvent.getSirenFusionne()) {
            etablissementRepository.deleteBySiren(siren);
            contactRepository.deleteBySiren(siren);
        }
    }
}
