package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.etablissement.EtablissementModifieEvent;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
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
        etablissementEntity.getContact().setAdresse(etablissementModifieEvent.getAdresseContact());
        etablissementEntity.getContact().setMail(etablissementModifieEvent.getMailContact());
        etablissementEntity.getContact().setTelephone(etablissementModifieEvent.getTelephoneContact());

        etablissementRepository.save(etablissementEntity);
    }
}
