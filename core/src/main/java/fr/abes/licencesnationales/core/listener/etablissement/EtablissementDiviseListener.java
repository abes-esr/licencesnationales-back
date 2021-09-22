package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementDiviseListener implements ApplicationListener<EtablissementDiviseEventEntity> {

    private final EtablissementService service;

    public EtablissementDiviseListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void onApplicationEvent(EtablissementDiviseEventEntity etablissementDiviseEvent) {
        /* attention a bien supprimer l'ancien établissement avant la création en cas de transfert de contact
        (pour les vérification de doublons mail / siren) */
        service.deleteBySiren(etablissementDiviseEvent.getAncienSiren());
        service.saveAll(etablissementDiviseEvent.getEtablissementDivises());
    }
}
