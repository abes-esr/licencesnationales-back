package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementEventDTO;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementDiviseEvent;
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
        for (EtablissementEventDTO etablissementEventDTODivise :
                etablissementDiviseEvent.getEtablissementEventDTOS()) {
            ContactEntity contactEntity =
                    new ContactEntity(null,
                            etablissementEventDTODivise.getNomContact(),
                            etablissementEventDTODivise.getPrenomContact(),
                            etablissementEventDTODivise.getMailContact(),
                            etablissementEventDTODivise.getMotDePasse(),
                            etablissementEventDTODivise.getTelephoneContact(),
                            etablissementEventDTODivise.getAdresseContact(),
                            etablissementEventDTODivise.getBoitePostaleContact(),
                            etablissementEventDTODivise.getCodePostalContact(),
                            etablissementEventDTODivise.getCedexContact(),
                            etablissementEventDTODivise.getVilleContact(),
                            etablissementEventDTODivise.getRoleContact());
            service.save(new EtablissementEntity(null,
                    etablissementEventDTODivise.getNom(),
                    etablissementEventDTODivise.getSiren(),
                    etablissementEventDTODivise.getTypeEtablissement(),
                    etablissementEventDTODivise.getIdAbes(),
                    contactEntity,
                    null));

        }
    }
}
