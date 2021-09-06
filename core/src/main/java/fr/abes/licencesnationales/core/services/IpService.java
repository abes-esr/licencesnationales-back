package fr.abes.licencesnationales.core.services;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import fr.abes.licencesnationales.core.dto.ip.IpContains;
import fr.abes.licencesnationales.core.dto.ip.IpModifieeDto;
import fr.abes.licencesnationales.core.entities.IpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
import fr.abes.licencesnationales.core.repository.IpRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class IpService {

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    public IpService() {
    }

    public List<IpContains> isAccesExistInList(IpEntity acces, List<IpEntity> accesList) {
        Integer contains = 0;
        Integer reserved = 0;
        List<IpContains> listIpContains = new ArrayList<IpContains>();

        reserved = acces.isReserved();
        if (reserved > 0){
            listIpContains.add(new IpContains(acces, null, reserved));
            return listIpContains;
        }
        for (IpEntity a : accesList) {
            // On ne compare pas l'accès à lui même...
            log.info("a.getIp() = " + a.getIp());
            log.info("acces.getIp() = " + acces.getIp());
            if (!a.getId().equals(acces.getId())){
                // On teste si l'adresse existe dans la base
                contains = acces.contains(a);
                log.info("contains =  "+ contains);
                if (contains > 0) {
                    listIpContains.add(new IpContains(acces, a, contains));
                    log.info("acces, a, contains = "+ acces, a, contains);
                }
            }
        }
        log.info("listIpContains.size = " + listIpContains.size());
        return listIpContains;
    }


    public List<IpContains> isAccesExist(IpEntity acces) {
        log.info("accesList = " + ipRepository.findAllIp().toString());
        return isAccesExistInList(acces, ipRepository.findAll());
    }

    public void checkDoublonIpAjouteeDto(IpAjouteeDto ipAjouteeDto) throws IpException {
        log.info("DEBUT checkDoublonIpAjouteeDto ");
        IpEntity ipEntity = new IpEntity();
        ipEntity.setIp(ipAjouteeDto.getIp());
        ipEntity.setTypeAcces(ipAjouteeDto.getTypeAcces());
        ipEntity.setTypeIp(ipAjouteeDto.getTypeIp());
        checkDoublonIp(ipEntity);
    }

    public void checkDoublonIpModifieeDto(IpModifieeDto ipModifieeDto) throws IpException {
        log.info("DEBUT checkDoublonIpModifieeDto ");
        IpEntity ipEntity = new IpEntity();
        ipEntity.setIp(ipModifieeDto.getIp());
        ipEntity.setTypeAcces(ipModifieeDto.getTypeAcces());
        ipEntity.setTypeIp(ipModifieeDto.getTypeIp());
        checkDoublonIp(ipEntity);
    }

    public void checkDoublonIp(IpEntity ipEntity) throws IpException {
        log.info("DEBUT checkDoublonIp");
        List<String> erreursList = new ArrayList<>();
        List<IpEntity> accesList = new ArrayList<>(); //une liste vide
        List<IpContains> listIpContains;
        List<List<IpContains>> listAllIpContains = new ArrayList<>();

        // Check les collisions avec les IP de la BDD
        listIpContains = isAccesExist(ipEntity);
        log.info("listIpContains.size2 = " + listIpContains.size());
        if (!listIpContains.isEmpty()) {
            listAllIpContains.add(listIpContains);
        }

        // Check les collisions avec les IP de la demande en cours
        listIpContains = isAccesExistInList(ipEntity, accesList);
        if (!listIpContains.isEmpty()) {
            listAllIpContains.add(listIpContains);
        }
        accesList.add(ipEntity);
        if (!listAllIpContains.isEmpty()) {
            String erreurMsg;
            for (List<IpContains> listIpsContains : listAllIpContains) {
                for (IpContains ipContains : listIpsContains) {
                    String nomEtab = "";
                    try {
                        nomEtab = etablissementRepository.findEtablissementEntityByIpsContains(ipContains.getDBAcces()).getName();
                    }
                    catch (Exception e){
                        log.error("Bdd : données incohérentes sur table Etablissement_ips => nomEtab = " + nomEtab);
                    }
                    erreurMsg = ((ipContains.getErreurAcces().getTypeAcces().equals("ip")) ? "L'adresse IP" : "La plage d'adresses IP") + " '" + ipContains.getErreurAcces().getIp() + "' ";
                    erreursList.add(erreurMsg);
                    // Si c'est une IP réservée
                    if (ipContains.getContains() == Constant.IP_RESERVED) {
                        erreursList.add("est une adresse réservée.");
                    } // détermine si l'adresse à déjà été enregistré dans la BDD ou non.
                    else if (ipContains.getDBAcces().getDateCreation() != null) {
                        switch (ipContains.getContains()) {
                            case Constant.IP_SAME:
                                erreursList.add("est déjà déclarée par l'établissement " + nomEtab);
                                break;
                            case Constant.IP_CONTAINED:
                                erreursList.add("est contenu dans la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' de l'établissement  " + nomEtab);
                                break;
                            case Constant.IP_CONTAINS:
                                erreursList.add("contient " + ((ipContains.getDBAcces().getTypeAcces().equals("ip")) ? " l'adresse IP" : "la plage d'adresses IP") + " '" + ipContains.getDBAcces().getIp() + "' de l'établissement " + nomEtab);
                                break;
                            case Constant.IP_CROSS:
                                erreursList.add("chevauche la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' de l'établissement  " + nomEtab);
                                break;
                        }
                    } else {
                        switch (ipContains.getContains()) {
                            case Constant.IP_SAME:
                                erreursList.add("est déjà présente dans votre demande de test en cours.");
                                break;
                            case Constant.IP_CONTAINED:
                                erreursList.add("est contenu dans la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' présente dans votre demande de test en cours.");
                                break;
                            case Constant.IP_CONTAINS:
                                erreursList.add("contient " + ((ipContains.getDBAcces().getTypeAcces().equals("ip")) ? " l'adresse IP" : "la plage d'adresses IP") + " '" + ipContains.getDBAcces().getIp() + "' présente dans votre demande de test en cours.");
                                break;
                            case Constant.IP_CROSS:
                                erreursList.add("chevauche la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' présente dans votre demande de test en cours.");
                                break;
                        }
                    }
                }
            }
        }
        if (!erreursList.isEmpty()) {
            throw new IpException(StringUtils.joinWith("\n", erreursList));
        }
    }

}
