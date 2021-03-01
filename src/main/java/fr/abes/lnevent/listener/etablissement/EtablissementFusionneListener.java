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

    public EtablissementFusionneListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

        EtablissementDTO etablissementDTOFusione = etablissementFusionneEvent.getEtablissementDTO();
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissementDTOFusione.getNomContact(),
                        etablissementDTOFusione.getPrenomContact(),
                        etablissementDTOFusione.getTelephoneContact(),
                        etablissementDTOFusione.getMailContact(),
                        etablissementDTOFusione.getAdresseContact());

        etablissementRepository.save(new EtablissementEntity(null,
                etablissementDTOFusione.getNom(),
                etablissementDTOFusione.getAdresse(),
                etablissementDTOFusione.getSiren(),
                etablissementDTOFusione.getMotDePasse(),
                etablissementDTOFusione.getTypeEtablissement(),
                etablissementDTOFusione.getIdAbes(),
                contactEntity,
                null));

        for (String siren :
                etablissementFusionneEvent.getSirenFusionne()) {
            etablissementRepository.deleteBySiren(siren);
        }
    }
}
