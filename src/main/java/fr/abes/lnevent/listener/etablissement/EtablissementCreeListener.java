package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementCreeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementCreeListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    public void onApplicationEvent(EtablissementCreeEvent etablissementCreeEvent) {
        EtablissementDTO etablissement = etablissementCreeEvent.getEtablissement();
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissement.getNomContact(),
                        etablissement.getPrenomContact(),
                        etablissement.getMailContact(),
                        etablissement.getMotDePasse(),
                        etablissement.getTelephoneContact(),
                        etablissement.getAdresseContact(),
                        etablissement.getBoitePostaleContact(),
                        etablissement.getCodePostalContact(),
                        etablissement.getCedexContact(),
                        etablissement.getVilleContact(),
                        etablissement.getRoleContact());
        EtablissementEntity etablissementEntity =
                new EtablissementEntity(null,
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
