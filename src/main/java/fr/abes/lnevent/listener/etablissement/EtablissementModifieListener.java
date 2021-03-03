package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.etablissement.EtablissementModifieEvent;
import fr.abes.lnevent.repository.ContactRepository;
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
        EtablissementDTO etablissement = etablissementModifieEvent.getEtablissement();
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissement.getNomContact(),
                        etablissement.getPrenomContact(),
                        etablissement.getMailContact(),
                        etablissement.getTelephoneContact(),
                        etablissement.getAdresseContact(),
                        etablissement.getBoitePostaleContact(),
                        etablissement.getCodePostalContact(),
                        etablissement.getCedexContact(),
                        etablissement.getVilleContact());
        EtablissementEntity etablissementEntity =
                new EtablissementEntity(
                        etablissementModifieEvent.getIdEtablissement(),
                        etablissement.getNom(),
                        etablissement.getSiren(),
                        etablissement.getMotDePasse(),
                        etablissement.getTypeEtablissement(),
                        etablissement.getIdAbes(),
                        contactEntity,
                        null);

        etablissementRepository.save(etablissementEntity);
    }
}
