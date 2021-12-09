package fr.abes.licencesnationales.core.services;


import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.exception.UnknownIpException;
import fr.abes.licencesnationales.core.exception.UnknownStatutException;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.repository.ip.IpV4Repository;
import fr.abes.licencesnationales.core.repository.ip.IpV6Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class IpService {

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private IpV4Repository ipV4Repository;

    @Autowired
    private IpV6Repository ipV6Repository;

    @Autowired
    private ReferenceService referenceService;
    /**
     * Récupère toutes les IP V4 et V6 confondus
     * @return
     */
    public List<IpEntity> getAll() {
        return ipRepository.findAll();
    }

    /**
     * Récupère toutes les IP V4
     * @return
     */
    public List<IpV4> getAllIpV4() {
        return ipV4Repository.findAll();
    }

    /**
     * Récupère toutes les IP V6
     * @return
     */
    public List<IpV6> getAllIpV6() {
        return ipV6Repository.findAll();
    }

    public void save(IpEntity ipEntity) {
        if (ipEntity.getId() != null) {
            ipEntity.setDateModification(new Date());
        }
        ipRepository.save(ipEntity);
    }

    public void deleteById(Integer id) {
        ipRepository.deleteById(id);
    }
    /**
     * Vérifies si une adresse IpV4 existe déjà dans la base de données.
     * L'IP existe si :
     *  - l'IP ou la plage d'IP est la même
     *  - L'IP ou la plage d'IP est inclus dans une plage déjà existante
     * @param ip IP à tester
     * @return Vrai si l'IP existe déjà, Faux sinon
     */
    public boolean isIpAlreadyExists(IpV4 ip) {
        List<IpV4> all = getAllIpV4();
        Iterator<IpV4> iter = all.iterator();
        while (iter.hasNext()) {
            IpV4 candidate = iter.next();
            if (candidate.getIpRange().contains(ip.getIpRange()) || candidate.getIpRange().overlaps(ip.getIpRange())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifies si une adresse IpV6 existe déjà dans la base de données.
     * L'IP existe si :
     * - l'IP ou la plage d'IP est la même
     * - L'IP ou la plage d'IP est inclus dans une plage déjà existante
     * @param ip
     * @return Vrai si l'IP existe déjà, Faux sinon
     */
    public boolean isIpAlreadyExists(IpV6 ip) {
        List<IpV6> all = getAllIpV6();
        Iterator<IpV6> iter = all.iterator();
        while (iter.hasNext()) {
            IpV6 candidate = iter.next();
            if (candidate.getIpRange().contains(ip.getIpRange()) || candidate.getIpRange().overlaps(ip.getIpRange())) {
                return true;
            }
        }
        return false;
    }

    public EtablissementEntity getEtablissementByIp(Integer id) throws UnknownIpException, UnknownEtablissementException {
        Optional<IpEntity> ip = ipRepository.getFirstById(id);
        if (!ip.isPresent()) {
            throw new UnknownIpException("L'IP " + id + " n'existe pas");
        }
        IpEntity ipEntity = ip.get();
        EtablissementEntity etab = ipEntity.getEtablissement();
        if (etab == null) {
            throw new UnknownEtablissementException("L'IP " + ipEntity.getIp() + " n'est pas rattachée à un établissement");
        }
        return etab;
    }

    public IpEntity getFirstById(Integer id) throws UnknownIpException {
        Optional<IpEntity> ipEntity = ipRepository.getFirstById(id);
        if (!ipEntity.isPresent()) {
            throw new UnknownIpException("L'IP " + id + " n'existe pas");
        }
        return ipEntity.get();
    }

    public List<IpEntity> getIpValidationOlderThanOneYear() throws UnknownStatutException {
        StatutEntity statut = referenceService.findStatutById(Constant.STATUT_IP_NOUVELLE);
        Calendar dateJour = new GregorianCalendar();
        dateJour.add(Calendar.YEAR, -1);
        return ipRepository.getAllByStatutAndDateModificationIsBefore((StatutIpEntity) statut, dateJour.getTime());
    }
}
