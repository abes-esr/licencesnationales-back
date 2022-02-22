package fr.abes.licencesnationales.core.listener.etablissement;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEventEntity> {

    private final EtablissementService service;
    private final ReferenceService referenceService;

    public EtablissementFusionneListener(EtablissementService service, ReferenceService referenceService) {
        this.service = service;
        this.referenceService = referenceService;
    }

    @SneakyThrows
    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEventEntity event) {
        // Attention à bien respecter l'ordre des arguments
        ContactEntity contactEntity = new ContactEntity(event.getNomContact(),
                event.getPrenomContact(),
                event.getAdresseContact(),
                event.getBoitePostaleContact(),
                event.getCodePostalContact(),
                event.getVilleContact(),
                event.getCedexContact(),
                event.getTelephoneContact(),
                event.getMailContact(),
                event.getMotDePasse());

        EtablissementEntity etab = new EtablissementEntity(event.getNomEtab(), event.getSiren(), referenceService.findTypeEtabByLibelle(event.getTypeEtablissement()), event.getIdAbes(), contactEntity);

        for (String siren : event.getSirenAnciensEtablissements()) {
            EtablissementEntity etablissementEntity = service.getFirstBySiren(siren);
            etab.ajouterIps(etablissementEntity.getIps());

            service.deleteBySiren(siren);
        }

        if (service.existeMail(etab.getContact().getMail())) {
            throw new MailDoublonException(String.format(Constant.ERROR_MAIL_DOUBLON,etab.getContact().getMail()));
        }
        if (service.existeSiren(etab.getSiren())) {
            throw new SirenExistException(String.format(Constant.ERROR_ETAB_DOUBLON,etab.getSiren()));
        }

        service.save(etab);
    }
}
