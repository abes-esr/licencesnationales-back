package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
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
        //verifier que le mail et le siren des nouveaux établissements ne sont pas déjà en base
        for (EtablissementEntity etab : etablissementDiviseEvent.getEtablissementDivises()) {
            if (service.existeMail(etab.getContact().getMail())) {
                throw new MailDoublonException("L'adresse mail " + etab.getContact().getMail() + " renseignée est déjà utilisée. Veuillez renseigner une autre adresse mail.");
            }
            if (service.existeSiren(etab.getSiren())) {
                throw new SirenExistException("L'établissement " + etab.getSiren() + " existe déjà");
            }
        }
        service.saveAll(etablissementDiviseEvent.getEtablissementDivises());
    }
}
