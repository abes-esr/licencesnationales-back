package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEventEntity> {

    private final EtablissementService service;

    private final ReferenceService referenceService;

    public EtablissementModifieListener(EtablissementService service, ReferenceService referenceService) {
        this.service = service;
        this.referenceService = referenceService;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void onApplicationEvent(EtablissementModifieEventEntity event) {
        EtablissementEntity etab = service.getFirstBySiren(event.getSiren());
        ContactEntity contact = etab.getContact();

        // Nom
        if(event.getNomEtab() != null)
            etab.setNom(event.getNomEtab());

        // Type d'établissement
        if(event.getTypeEtablissement() != null)
            etab.setTypeEtablissement(referenceService.findTypeEtabByLibelle(event.getTypeEtablissement()));

        //statut de validation  de l'établissement
        etab.setValide(event.isValide());

        // Contact - nom
        contact.setNom(event.getNomContact());

        // Contact - prénom
        contact.setPrenom(event.getPrenomContact());

        // Contact - téléphone
        contact.setTelephone(event.getTelephoneContact());

        // Contact - mail
        contact.setMail(event.getMailContact());

        // Contact - adresse
        contact.setAdresse(event.getAdresseContact());

        // Contact - boîte postale
        contact.setBoitePostale(event.getBoitePostaleContact());

        // Contact - code postal
        contact.setCodePostal(event.getCodePostalContact());

        // Contact - cedex
        contact.setCedex(event.getCedexContact());

        // Contact - ville
        contact.setVille(event.getVilleContact());

        service.save(etab);
    }
}
