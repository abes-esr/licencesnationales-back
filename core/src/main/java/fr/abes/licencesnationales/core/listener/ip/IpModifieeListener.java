package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpModifieeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.UnknownStatutException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpModifieeListener implements ApplicationListener<IpModifieeEventEntity> {

    private final EtablissementService service;
    private final ReferenceService referenceService;


    public IpModifieeListener(EtablissementService service, ReferenceService referenceService) {
        this.service = service;
        this.referenceService = referenceService;
    }


    @Override
    @SneakyThrows
    public void onApplicationEvent(IpModifieeEventEntity ipModifieeEvent) {
        EtablissementEntity etablissementEntity = service.getFirstBySiren(ipModifieeEvent.getSiren());
        StatutIpEntity statut = null;
        if (ipModifieeEvent.getStatut() != null) {
            statut = (StatutIpEntity) referenceService.findStatutByLibelle(ipModifieeEvent.getStatut());
        }
        StatutIpEntity finalStatut = statut;
        etablissementEntity.getIps().stream().filter(ip -> ip.getId().equals(ipModifieeEvent.getId())).forEach(ipEntity -> {
            ipEntity.setIp(ipModifieeEvent.getIp());
            ipEntity.setCommentaires(ipModifieeEvent.getCommentaires());
            if (finalStatut != null) {
                ipEntity.setStatut(finalStatut);
            }
        });
        service.save(etablissementEntity);
    }
}
