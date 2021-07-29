package fr.abes.licencesnationales.listener.etablissement;

import fr.abes.licencesnationales.dto.etablissement.EtablissementEventDTO;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EditeurEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementFusionneEvent;
import fr.abes.licencesnationales.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEvent> {

    private final EtablissementService service;

    public EtablissementFusionneListener(EtablissementService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

        Set<IpEntity> ipEntities = new HashSet<>();
        Set<EditeurEntity> editeurEntities = new HashSet<>();

        for (String siren :
                etablissementFusionneEvent.getSirenFusionne()) {
                EtablissementEntity etablissementEntity = service.getFirstBySiren(siren);
            if (etablissementEntity.getIps() != null) {
                ipEntities.addAll(etablissementEntity.getIps()
                        .stream().map(e -> new IpEntity(null, e.getIp(), e.getTypeAcces(), e.getTypeIp(), e.getCommentaires()))
                        .collect(Collectors.toSet()));
            }

            if (etablissementEntity.getEditeurs() != null) {
                editeurEntities.addAll(etablissementEntity.getEditeurs());
            }

            service.deleteBySiren(siren);
        }

        EtablissementEventDTO etablissementEventDTOFusione = etablissementFusionneEvent.getEtablissementEventDTO();
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissementEventDTOFusione.getNomContact(),
                        etablissementEventDTOFusione.getPrenomContact(),
                        etablissementEventDTOFusione.getMailContact(),
                        etablissementEventDTOFusione.getMotDePasse(),
                        etablissementEventDTOFusione.getTelephoneContact(),
                        etablissementEventDTOFusione.getAdresseContact(),
                        etablissementEventDTOFusione.getBoitePostaleContact(),
                        etablissementEventDTOFusione.getCodePostalContact(),
                        etablissementEventDTOFusione.getCedexContact(),
                        etablissementEventDTOFusione.getVilleContact(),
                        etablissementEventDTOFusione.getRoleContact());

        EtablissementEntity etablissementEntity = new EtablissementEntity(null,
                etablissementEventDTOFusione.getNom(),
                etablissementEventDTOFusione.getSiren(),
                etablissementEventDTOFusione.getTypeEtablissement(),
                etablissementEventDTOFusione.getIdAbes(),
                contactEntity,
                editeurEntities);
        etablissementEntity.setIps(ipEntities);

        service.save(etablissementEntity);

    }
}
