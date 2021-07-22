package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementModifieEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementModifieListener implements ApplicationListener<EtablissementModifieEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementModifieListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementModifieEvent etablissementModifieEvent) {
        EtablissementEntity etablissementEntity = etablissementRepository.getFirstBySiren(etablissementModifieEvent.getSiren());
        etablissementEntity.getContact().setNom(etablissementModifieEvent.getNomContact());
        etablissementEntity.getContact().setPrenom(etablissementModifieEvent.getPrenomContact());
        etablissementEntity.getContact().setAdresse(etablissementModifieEvent.getAdresseContact());
        etablissementEntity.getContact().setMail(etablissementModifieEvent.getMailContact());
        etablissementEntity.getContact().setTelephone(etablissementModifieEvent.getTelephoneContact());
        etablissementEntity.getContact().setBoitePostale(etablissementModifieEvent.getBoitePostaleContact());
        etablissementEntity.getContact().setCodePostal(etablissementModifieEvent.getCodePostalContact());
        etablissementEntity.getContact().setVille(etablissementModifieEvent.getVilleContact());
        etablissementEntity.getContact().setCedex(etablissementModifieEvent.getCedexContact());

        etablissementRepository.save(etablissementEntity);
    }
}
