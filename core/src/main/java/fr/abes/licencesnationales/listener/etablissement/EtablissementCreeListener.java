package fr.abes.licencesnationales.listener.etablissement;


import fr.abes.licencesnationales.dto.etablissement.EtablissementEventDTO;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementCreeEvent;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEvent> {

    private final EtablissementService service;

    public EtablissementCreeListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(EtablissementCreeEvent etablissementCreeEvent) {
        EtablissementEventDTO etablissement = etablissementCreeEvent.getEtablissement();
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
                        etablissement.getTypeEtablissement(),
                        etablissement.getIdAbes(),
                        contactEntity,
                        null);

        service.save(etablissementEntity);

    }
}
