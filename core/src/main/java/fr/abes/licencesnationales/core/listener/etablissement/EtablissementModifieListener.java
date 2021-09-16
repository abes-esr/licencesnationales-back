package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownTypeEtablissementException;
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
        if (event.getNomEtab() != null) {
            etab.setName(event.getNomEtab());
        }

        // Siren
        if (event.getSiren() != null) {
           etab.setSiren(event.getSiren());
        }

        // Type d'établissement
        if (event.getTypeEtablissement() != null) {
            Optional<TypeEtablissementEntity> type = typeEtabrepository.findFirstByLibelle(event.getTypeEtablissement());
            if (!type.isPresent()) {
                throw new UnknownTypeEtablissementException("Type d'établissement inconnu");
            }
            etab.setTypeEtablissement(type.get());
        }

        // Contact - nom
        if (event.getNomContact() != null) {
            contact.setNom(event.getNomContact());
        }

        // Contact - prénom
        if (event.getPrenomContact() != null) {
            contact.setPrenom(event.getPrenomContact());
        }

        // Contact - téléphone
        if (event.getTelephoneContact() != null) {
            contact.setTelephone(event.getTelephoneContact());
        }

        // Contact - mail
        if (event.getMailContact() != null) {
            contact.setMail(event.getMailContact());
        }

        // Contact - mot de passe
        if (event.getMotDePasse() != null) {
            contact.setMotDePasse(event.getMotDePasse());
        }

        // Contact - adresse
        if (event.getAdresseContact() != null) {
            contact.setAdresse(event.getAdresseContact());
        }

        // Contact - boîte postale
        if (event.getBoitePostaleContact() != null) {
            contact.setBoitePostale(event.getBoitePostaleContact());
        }

        // Contact - code postal
        if (event.getCodePostalContact() != null) {
            contact.setCodePostal(event.getCodePostalContact());
        }

        // Contact - cedex
        if (event.getCedexContact() != null) {
           contact.setCedex(event.getCedexContact());
        }

        // Contact - ville
        if (event.getVilleContact() != null) {
            contact.setVille(event.getVilleContact());
        }

        service.save(etab);
    }
}
