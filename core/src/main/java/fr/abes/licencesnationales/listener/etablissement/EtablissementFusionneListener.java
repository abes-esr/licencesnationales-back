package fr.abes.licencesnationales.listener.etablissement;

import fr.abes.licencesnationales.dto.etablissement.EtablissementDTO;
import fr.abes.licencesnationales.entities.ContactEntity;
import fr.abes.licencesnationales.entities.EditeurEntity;
import fr.abes.licencesnationales.entities.EtablissementEntity;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.event.etablissement.EtablissementFusionneEvent;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                        .stream().map(e -> new IpEntity(null, e.getIp(), e.getTypeAcces(), e.getTypeIp(), e.getCommentaires()))
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
                        etablissementDTOFusione.getMailContact(),
                        etablissementDTOFusione.getMotDePasse(),
                        etablissementDTOFusione.getTelephoneContact(),
                        etablissementDTOFusione.getAdresseContact(),
                        etablissementDTOFusione.getBoitePostaleContact(),
                        etablissementDTOFusione.getCodePostalContact(),
                        etablissementDTOFusione.getCedexContact(),
                        etablissementDTOFusione.getVilleContact(),
                        etablissementDTOFusione.getRoleContact());

        EtablissementEntity etablissementEntity = new EtablissementEntity(null,
                etablissementDTOFusione.getNom(),
                etablissementDTOFusione.getSiren(),
                etablissementDTOFusione.getTypeEtablissement(),
                etablissementDTOFusione.getIdAbes(),
                contactEntity,
                editeurEntities);
        etablissementEntity.setIps(ipEntities);

        etablissementRepository.save(etablissementEntity);

    }
}
