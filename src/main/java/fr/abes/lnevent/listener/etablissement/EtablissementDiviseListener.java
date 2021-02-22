package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementDiviseEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementDiviseListener implements ApplicationListener<EtablissementDiviseEvent> {

    private final EtablissementRepository etablissementRepository;
    private final ContactRepository contactRepository;

    public EtablissementDiviseListener(EtablissementRepository etablissementRepository, ContactRepository contactRepository) {
        this.etablissementRepository = etablissementRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementDiviseEvent etablissementDiviseEvent) {
        etablissementRepository.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        contactRepository.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        for (EtablissementDTO etablissementDTODivise :
                etablissementDiviseEvent.getEtablissementDTOS()) {
            etablissementRepository.save(new EtablissementEntity(null,
                    etablissementDTODivise.getNom(),
                    etablissementDTODivise.getAdresse(),
                    etablissementDTODivise.getSiren(),
                    etablissementDTODivise.getTypeEtablissement(),
                    etablissementDTODivise.getIdAbes()));

            ContactEntity contactEntity =
                    new ContactEntity(null,
                            etablissementDTODivise.getNomContact(),
                            etablissementDTODivise.getPrenomContact(),
                            etablissementDTODivise.getMailContact(),
                            etablissementDTODivise.getMotDePasse(),
                            etablissementDTODivise.getTelephoneContact(),
                            etablissementDTODivise.getAdresseContact(),
                            etablissementDTODivise.getSiren());

            contactRepository.save(contactEntity);
        }
    }
}
