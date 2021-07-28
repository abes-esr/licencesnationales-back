package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEvent> {

    private final EtablissementService service;

    public EtablissementModifieListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementModifieEvent etablissementModifieEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(etablissementModifieEvent.getSiren());

        etablissementEntity.getContact().setNom(etablissementModifieEvent.getNomContact());
        etablissementEntity.getContact().setPrenom(etablissementModifieEvent.getPrenomContact());
        etablissementEntity.getContact().setAdresse(etablissementModifieEvent.getAdresseContact());
        etablissementEntity.getContact().setMail(etablissementModifieEvent.getMailContact());
        etablissementEntity.getContact().setTelephone(etablissementModifieEvent.getTelephoneContact());
        etablissementEntity.getContact().setBoitePostale(etablissementModifieEvent.getBoitePostaleContact());
        etablissementEntity.getContact().setCodePostal(etablissementModifieEvent.getCodePostalContact());
        etablissementEntity.getContact().setVille(etablissementModifieEvent.getVilleContact());
        etablissementEntity.getContact().setCedex(etablissementModifieEvent.getCedexContact());

        service.save(etablissementEntity);
    }
}
