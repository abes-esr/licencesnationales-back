package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpModifieeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.UnknownStatutException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpModifieeListener implements ApplicationListener<IpModifieeEventEntity> {

    private final IpService service;
    private final ReferenceService referenceService;


    public IpModifieeListener(IpService service, ReferenceService referenceService) {
        this.service = service;
        this.referenceService = referenceService;
    }


    @Override
    @SneakyThrows
    public void onApplicationEvent(IpModifieeEventEntity ipModifieeEvent) {
        IpEntity ipEntity = service.getFirstById(ipModifieeEvent.getIpId());
        //cas ou l'admin modifie le statut de l'IP, qui passe de la dto au champ transcient de l'event
        if (ipModifieeEvent.getStatut() != null) {
            ipEntity.setStatut((StatutIpEntity) referenceService.findStatutByLibelle(ipModifieeEvent.getStatut()));
        }
        ipEntity.setIp(ipModifieeEvent.getIp());
        ipEntity.setCommentaires(ipModifieeEvent.getCommentaires());
        service.save(ipEntity);
    }
}
