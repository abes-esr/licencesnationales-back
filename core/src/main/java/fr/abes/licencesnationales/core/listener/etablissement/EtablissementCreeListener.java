package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEventEntity> {

    @Autowired
    private EtablissementService service;

    @Autowired
    private ReferenceService referenceService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(EtablissementCreeEventEntity event) {
        // Attention à bien respecter l'ordre des arguments
        ContactEntity contactEntity = new ContactEntity(event.getNomContact(),
                event.getPrenomContact(),
                event.getAdresseContact(),
                event.getBoitePostaleContact(),
                event.getCodePostalContact(),
                event.getVilleContact(),
                event.getCedexContact(),
                event.getTelephoneContact(),
                event.getMailContact(),
                event.getMotDePasse());

        EtablissementEntity etab = new EtablissementEntity(event.getNomEtab(), event.getSiren(), referenceService.findTypeEtabByLibelle(event.getTypeEtablissement()), event.getIdAbes(), contactEntity);

        service.save(etab);

    }
}
