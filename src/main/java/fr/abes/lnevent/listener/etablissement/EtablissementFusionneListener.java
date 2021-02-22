package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementFusionneEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementFusionneListener(EtablissementRepository etablissementRepository, ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

        EtablissementDTO etablissementDTOFusione = etablissementFusionneEvent.getEtablissementDTO();

        etablissementRepository.save(new EtablissementEntity(null,
                etablissementDTOFusione.getNom(),
                etablissementDTOFusione.getAdresse(),
                etablissementDTOFusione.getSiren(),
                etablissementDTOFusione.getTypeEtablissement(),
                etablissementDTOFusione.getIdAbes()));

        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissementDTOFusione.getNomContact(),
                        etablissementDTOFusione.getPrenomContact(),
                        etablissementDTOFusione.getMailContact(),
                        etablissementDTOFusione.getMotDePasse(),
                        etablissementDTOFusione.getTelephoneContact(),
                        etablissementDTOFusione.getAdresseContact(),
                        etablissementDTOFusione.getSiren());

        contactRepository.save(contactEntity);

        for (String siren :
                etablissementFusionneEvent.getSirenFusionne()) {
            etablissementRepository.deleteBySiren(siren);
            contactRepository.deleteBySiren(siren);
        }
    }
}
