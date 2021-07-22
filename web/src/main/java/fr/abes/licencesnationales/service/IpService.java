package fr.abes.licencesnationales.service;


import fr.abes.licencesnationales.constant.Constant;
import fr.abes.licencesnationales.dto.ip.IpAjouteeDTO;
import fr.abes.licencesnationales.dto.ip.IpContains;
import fr.abes.licencesnationales.dto.ip.IpModifieeDTO;
import fr.abes.licencesnationales.entities.IpEntity;
import fr.abes.licencesnationales.repository.EtablissementRepository;
import fr.abes.licencesnationales.repository.IpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private IpAjouteeDTO ipAjouteeDTO;
    private IpModifieeDTO ipModifieeDTO;


    public IpService() {
    }

    public IpService(IpAjouteeDTO ipAjouteeDTO) {
        this.ipAjouteeDTO = ipAjouteeDTO;
    }

    public IpService(IpModifieeDTO ipModifieeDTO) {
        this.ipModifieeDTO = ipModifieeDTO;
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

    public ResponseEntity<?> checkDoublonIpAjouteeDto(IpAjouteeDTO ipAjouteeDTO) {
        log.info("DEBUT checkDoublonIpAjouteeDto ");
        String ip = ipAjouteeDTO.getIp();
        IpEntity ipEntity = new IpEntity();
        ipEntity.setIp(ipAjouteeDTO.getIp());
        ipEntity.setTypeAcces(ipAjouteeDTO.getTypeAcces());
        ipEntity.setTypeIp(ipAjouteeDTO.getTypeIp());
        return checkDoublonIp(ipEntity);
    }

    public ResponseEntity<?> checkDoublonIpModifieeDto(IpModifieeDTO ipModifieeDTO) {
        log.info("DEBUT checkDoublonIpModifieeDto ");
        String ip = ipModifieeDTO.getIp();
        IpEntity ipEntity = new IpEntity();
        ipEntity.setIp(ipModifieeDTO.getIp());
        ipEntity.setTypeAcces(ipModifieeDTO.getTypeAcces());
        ipEntity.setTypeIp(ipModifieeDTO.getTypeIp());
        return checkDoublonIp(ipEntity);
    }

    public ResponseEntity<?> checkDoublonIp(IpEntity ipEntity) {
        ResponseEntity responseEntity = new ResponseEntity("Contrôle doublon ip/plage ok",HttpStatus.OK);
        log.info("DEBUT checkDoublonIp");
        List<IpEntity> accesList = new ArrayList<>(); //une liste vide
        List<IpContains> listIpContains = new ArrayList<>();
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
            String erreurMsg = "";
            List<String> erreursList = new ArrayList<>();

            for (List<IpContains> listIpsContains : listAllIpContains) {
                for (IpContains ipContains : listIpsContains) {
                    String nomEtab = "";
                    try {
                        nomEtab = etablissementRepository.findEtablissementEntityByIpsContains(ipContains.getDBAcces()).getName();
                    }
                    catch (Exception e){
                        log.error("Bdd : données incohérentes sur table Etablissement_ips => nomEtab = " + nomEtab);
                    }
                    //String nomEtab = etablissementRepository.findEtablissementEntityByIpsContains(ipContains.getDBAcces()).getName();
                    erreurMsg = ((ipContains.getErreurAcces().getTypeAcces().equals("ip")) ? "L'adresse IP" : "La plage d'adresses IP") + " '" + ipContains.getErreurAcces().getIp() + "' ";
                    log.info("erreurMsg = " + erreurMsg);
                    responseEntity = badRequest(erreurMsg);
                    // Si c'est une IP réservée
                    if (ipContains.getContains() == Constant.IP_RESERVED) {
                        erreurMsg = erreurMsg + "est une adresse réservée.";
                        log.info("erreurMsg2 = " + erreurMsg);
                        responseEntity = badRequest(erreurMsg);
                    } // détermine si l'adresse à déjà été enregistré dans la BDD ou non.
                    else if (ipContains.getDBAcces().getDateCreation() != null) {
                        switch (ipContains.getContains()) {
                            case Constant.IP_SAME:
                                erreurMsg = erreurMsg + "est déjà déclarée par l'établissement " + nomEtab;
                                log.info("erreurMsg3 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                            case Constant.IP_CONTAINED:
                                erreurMsg = erreurMsg + "est contenu dans la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' de l'établissement  " + nomEtab;
                                log.info("erreurMsg4 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                            case Constant.IP_CONTAINS:
                                erreurMsg = erreurMsg + "contient " + ((ipContains.getDBAcces().getTypeAcces().equals("ip")) ? " l'adresse IP" : "la plage d'adresses IP") + " '" + ipContains.getDBAcces().getIp() + "' de l'établissement " + nomEtab;
                                log.info("erreurMsg5 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                            case Constant.IP_CROSS:
                                erreurMsg = erreurMsg + "chevauche la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' de l'établissement  " + nomEtab;
                                log.info("erreurMsg6 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                        }
                    } else {
                        switch (ipContains.getContains()) {
                            case Constant.IP_SAME:
                                erreurMsg = erreurMsg + "est déjà présente dans votre demande de test en cours.";
                                log.info("erreurMsg7 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                            case Constant.IP_CONTAINED:
                                erreurMsg = erreurMsg + "est contenu dans la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' présente dans votre demande de test en cours.";
                                log.info("erreurMsg8 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                            case Constant.IP_CONTAINS:
                                erreurMsg = erreurMsg + "contient " + ((ipContains.getDBAcces().getTypeAcces().equals("ip")) ? " l'adresse IP" : "la plage d'adresses IP") + " '" + ipContains.getDBAcces().getIp() + "' présente dans votre demande de test en cours.";
                                log.info("erreurMsg9 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                            case Constant.IP_CROSS:
                                erreurMsg = erreurMsg + "chevauche la plage d'adresse '" + ipContains.getDBAcces().getIp() + "' présente dans votre demande de test en cours.";
                                log.info("erreurMsg10 = " + erreurMsg);
                                responseEntity = badRequest(erreurMsg);
                                break;
                        }
                    }
                }
            }
        }
        log.info("ResponseEntity =" + responseEntity.toString());
        return responseEntity;
    }
    public ResponseEntity<?> badRequest(String msgError) {
        return ResponseEntity
                .badRequest()
                .body(msgError);
    }
}
