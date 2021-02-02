package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.Etablissement;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.repository.entities.ContactRow;
import fr.abes.lnevent.repository.entities.EtablissementRow;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementDiviseEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementDiviseListener implements ApplicationListener<EtablissementDiviseEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementDiviseListener(EtablissementRepository etablissementRepository, ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementDiviseEvent etablissementDiviseEvent) {
        etablissementRepository.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        contactRepository.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        for (Etablissement etablissementDivise :
                etablissementDiviseEvent.getEtablissements()) {
            etablissementRepository.save(new EtablissementRow(null,
                    etablissementDivise.getNom(),
                    etablissementDivise.getAdresse(),
                    etablissementDivise.getSiren(),
                    etablissementDivise.getTypeEtablissement(),
                    etablissementDivise.getIdAbes()));

            ContactRow contactRow =
                    new ContactRow(null,
                            etablissementDivise.getNomContact(),
                            etablissementDivise.getPrenomContact(),
                            etablissementDivise.getMailContact(),
                            etablissementDivise.getMotDePasse(),
                            etablissementDivise.getTelephoneContact(),
                            etablissementDivise.getAdresseContact(),
                            etablissementDivise.getSiren());

            contactRepository.save(contactRow);
        }
    }
}
