package fr.abes.licencesnationales.core.listener.etablissement;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEventEntity> {

    private final EtablissementService service;
    private final ReferenceService referenceService;

    public EtablissementFusionneListener(EtablissementService service, ReferenceService referenceService) {
        this.service = service;
        this.referenceService = referenceService;
    }

    @SneakyThrows
    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEventEntity event) {
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

        for (String siren : event.getSirenAnciensEtablissements()) {
            EtablissementEntity etablissementEntity = service.getFirstBySiren(siren);
            etab.ajouterIps(etablissementEntity.getIps());
            etab.ajouterEditeurs(etablissementEntity.getEditeurs());

            service.deleteBySiren(siren);
        }
        service.save(etab);
    }
}
