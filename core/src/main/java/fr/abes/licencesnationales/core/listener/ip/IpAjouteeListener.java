package fr.abes.licencesnationales.core.listener.ip;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.ip.event.IpCreeEventEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpDoublonException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.IpService;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IpAjouteeListener implements ApplicationListener<IpCreeEventEntity> {
    private final StatutRepository statutRepository;
    private final EtablissementService etablissementService;
    private final IpService ipService;

    public IpAjouteeListener(StatutRepository statutRepository, EtablissementService etablissementService, IpService ipService) {
        this.statutRepository = statutRepository;
        this.etablissementService = etablissementService;
        this.ipService = ipService;
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(IpCreeEventEntity ipCreeEvent) {
        EtablissementEntity etablissementEntity = etablissementService.getFirstBySiren(ipCreeEvent.getSiren());
        IpEntity ip;
        if (ipCreeEvent.getTypeIp().equals(IpType.IPV4)) {
            ip = new IpV4(ipCreeEvent.getIp(), ipCreeEvent.getCommentaires());
            if (ipService.isIpAlreadyExists((IpV4) ip)) {
                throw new IpDoublonException("L'IP " + ip.getIp() + " est déjà utilisée");
            }
        } else {
            ip = new IpV6(ipCreeEvent.getIp(), ipCreeEvent.getCommentaires());
            if (ipService.isIpAlreadyExists((IpV6) ip)) {
                throw new IpDoublonException("L'IP " + ip.getIp() + " est déjà utilisée");
            }
        }
        ip.setStatut((StatutIpEntity) statutRepository.findById(Constant.STATUT_IP_NOUVELLE).get());
        etablissementEntity.ajouterIp(ip);
        etablissementService.save(etablissementEntity);
    }
}
