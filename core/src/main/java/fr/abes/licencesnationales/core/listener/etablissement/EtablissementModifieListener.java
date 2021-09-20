package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.TypeEtablissementRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEventEntity> {

    private final EtablissementService service;

    @Autowired
    private TypeEtablissementRepository typeEtabrepository;

    @Autowired
    private StatutRepository statutRepository;

    public EtablissementModifieListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    @SneakyThrows
    public void onApplicationEvent(EtablissementModifieEventEntity event) {
        EtablissementEntity etab = service.getFirstBySiren(event.getSiren());
        ContactEntity contact = etab.getContact();

        // Nom
        etab.setName(event.getNomEtab());

        // Type d'établissement
        Optional<TypeEtablissementEntity> type = typeEtabrepository.findFirstByLibelle(event.getTypeEtablissement());
        if (!type.isPresent()) {
            throw new UnknownTypeEtablissementException("Type d'établissement inconnu");
        }
        etab.setTypeEtablissement(type.get());

        // Contact - nom
        contact.setNom(event.getNomContact());

        // Contact - prénom
        contact.setPrenom(event.getPrenomContact());

        // Contact - téléphone
        contact.setTelephone(event.getTelephoneContact());

        // Contact - mail
        contact.setMail(event.getMailContact());

        // Contact - mot de passe
        contact.setMotDePasse(event.getMotDePasse());

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
