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

    public EtablissementDiviseListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementDiviseEvent etablissementDiviseEvent) {
        etablissementRepository.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        for (EtablissementDTO etablissementDTODivise :
                etablissementDiviseEvent.getEtablissementDTOS()) {
            ContactEntity contactEntity =
                    new ContactEntity(null,
                            etablissementDTODivise.getNomContact(),
                            etablissementDTODivise.getPrenomContact(),
                            etablissementDTODivise.getMailContact(),
                            etablissementDTODivise.getTelephoneContact(),
                            etablissementDTODivise.getAdresseContact(),
                            etablissementDTODivise.getBoitePostaleContact(),
                            etablissementDTODivise.getCodePostalContact(),
                            etablissementDTODivise.getCedexContact(),
                            etablissementDTODivise.getVilleContact());
            etablissementRepository.save(new EtablissementEntity(null,
                    etablissementDTODivise.getNom(),
                    etablissementDTODivise.getSiren(),
                    etablissementDTODivise.getMotDePasse(),
                    etablissementDTODivise.getTypeEtablissement(),
                    etablissementDTODivise.getIdAbes(),
                    contactEntity,
                    null));
        }
    }
}
