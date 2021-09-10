package fr.abes.licencesnationales.core.listener.etablissement;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.event.etablissement.EtablissementFusionneEvent;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class EtablissementFusionneListener implements ApplicationListener<EtablissementFusionneEvent> {

    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public EtablissementFusionneListener(EtablissementService service, UtilsMapper utilsMapper) {
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    @Transactional
    public void onApplicationEvent(EtablissementFusionneEvent etablissementFusionneEvent) {

       /* Set<IpEntity> ipEntities = new HashSet<>();
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
        EtablissementEntity etablissementEntity = utilsMapper.map(etablissementFusionneEvent, EtablissementEntity.class);
        etablissementEntity.setEditeurs(editeurEntities);
        etablissementEntity.setIps(ipEntities);

        service.save(etablissementEntity);*/

    }
}
