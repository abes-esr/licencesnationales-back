package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementDTO;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementDiviseEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementDiviseListener implements ApplicationListener<EtablissementDiviseEvent> {

    private final EtablissementService service;

    public EtablissementDiviseListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementDiviseEvent etablissementDiviseEvent) {
        service.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        for (EtablissementDTO etablissementDTODivise :
                etablissementDiviseEvent.getEtablissementDTOS()) {
            ContactEntity contactEntity =
                    new ContactEntity(null,
                            etablissementDTODivise.getNomContact(),
                            etablissementDTODivise.getPrenomContact(),
                            etablissementDTODivise.getMailContact(),
                            etablissementDTODivise.getMotDePasse(),
                            etablissementDTODivise.getTelephoneContact(),
                            etablissementDTODivise.getAdresseContact(),
                            etablissementDTODivise.getBoitePostaleContact(),
                            etablissementDTODivise.getCodePostalContact(),
                            etablissementDTODivise.getCedexContact(),
                            etablissementDTODivise.getVilleContact(),
                            etablissementDTODivise.getRoleContact());
            service.save(new EtablissementEntity(null,
                    etablissementDTODivise.getNom(),
                    etablissementDTODivise.getSiren(),
                    etablissementDTODivise.getTypeEtablissement(),
                    etablissementDTODivise.getIdAbes(),
                    contactEntity,
                    null));

        }
    }
}
