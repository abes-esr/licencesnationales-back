package fr.abes.licencesnationales.core.listener.etablissement;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EtablissementCreeListener implements ApplicationListener<EtablissementCreeEventEntity> {
    private final StatutRepository statutRepository;
    private final EtablissementService service;
    private final UtilsMapper utilsMapper;

    public EtablissementCreeListener(StatutRepository statutRepository, EtablissementService service, UtilsMapper utilsMapper) {
        this.statutRepository = statutRepository;
        this.service = service;
        this.utilsMapper = utilsMapper;
    }

    @Override
    public void onApplicationEvent(EtablissementCreeEventEntity etablissementCreeEvent) {
        EtablissementEntity etablissementEntity = utilsMapper.map(etablissementCreeEvent, EtablissementEntity.class);
        etablissementEntity.setStatut((StatutEtablissementEntity) statutRepository.findById(Constant.STATUT_ETAB_NOUVEAU).get());
        service.save(etablissementEntity);

    }
}
