package fr.abes.lnevent.listener.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.entities.EditeurEntity;
import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.repository.ContactRepository;
import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.event.etablissement.EtablissementFusionneEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEvent> {

    private final EtablissementRepository etablissementRepository;

    public EtablissementFusionneListener(EtablissementRepository etablissementRepository) {
        this.etablissementRepository = etablissementRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

        Set<IpEntity> ipEntities = new HashSet<>();
        Set<EditeurEntity> editeurEntities = new HashSet<>();

        for (String siren :
                etablissementFusionneEvent.getSirenFusionne()) {

            if (etablissementRepository.getFirstBySiren(siren).getIps() != null) {
                ipEntities.addAll(etablissementRepository.getFirstBySiren(siren).getIps()
                        .stream().map(e -> new IpEntity(null, e.getIp()))
                        .collect(Collectors.toSet()));
            }

            if (etablissementRepository.getFirstBySiren(siren).getEditeurs() != null) {
                editeurEntities.addAll(etablissementRepository.getFirstBySiren(siren).getEditeurs());
            }

            etablissementRepository.deleteBySiren(siren);
        }

        EtablissementDTO etablissementDTOFusione = etablissementFusionneEvent.getEtablissementDTO();
        ContactEntity contactEntity =
                new ContactEntity(null,
                        etablissementDTOFusione.getNomContact(),
                        etablissementDTOFusione.getPrenomContact(),
                        etablissementDTOFusione.getTelephoneContact(),
                        etablissementDTOFusione.getMailContact(),
                        etablissementDTOFusione.getAdresseContact(),
                        etablissementDTOFusione.getBoitePostaleContact(),
                        etablissementDTOFusione.getCodePostalContact(),
                        etablissementDTOFusione.getCedexContact(),
                        etablissementDTOFusione.getVilleContact());

        EtablissementEntity etablissementEntity = new EtablissementEntity(null,
                etablissementDTOFusione.getNom(),
                etablissementDTOFusione.getSiren(),
                etablissementDTOFusione.getMotDePasse(),
                etablissementDTOFusione.getTypeEtablissement(),
                etablissementDTOFusione.getIdAbes(),
                contactEntity,
                editeurEntities);
        etablissementEntity.setIps(ipEntities);

        etablissementRepository.save(etablissementEntity);

    }
}
