package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpDoublonException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import fr.abes.licencesnationales.core.services.ReferenceService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpCreeEventEntity> {
    private final ReferenceService referenceService;
    private final EtablissementService etablissementService;
    private final IpService ipService;

    public IpAjouteeListener(ReferenceService referenceService, EtablissementService etablissementService, IpService ipService) {
        this.referenceService = referenceService;
        this.etablissementService = etablissementService;
        this.ipService = ipService;
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(IpCreeEventEntity ipCreeEvent) {
        EtablissementEntity etablissementEntity = etablissementService.getFirstBySiren(ipCreeEvent.getSiren());
        StatutIpEntity statut = (StatutIpEntity) referenceService.findStatutById(Constant.STATUT_IP_NOUVELLE);
        IpEntity ip;
        if (ipCreeEvent.getTypeIp().equals(IpType.IPV4)) {
            ip = new IpV4(ipCreeEvent.getIp(), ipCreeEvent.getCommentaires(), statut);
            if (ipService.isIpAlreadyExists((IpV4) ip)) {
                throw new IpDoublonException(String.format(Constant.ERROR_IP_DOUBLON,ip.getIp()));
            }
        } else {
            ip = new IpV6(ipCreeEvent.getIp(), ipCreeEvent.getCommentaires(), statut);
            if (ipService.isIpAlreadyExists((IpV6) ip)) {
                throw new IpDoublonException(String.format(Constant.ERROR_IP_DOUBLON,ip.getIp()));
            }
        }
        etablissementEntity.ajouterIp(ip);
        etablissementService.save(etablissementEntity);
    }
}
