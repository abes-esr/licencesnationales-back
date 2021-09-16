package fr.abes.licencesnationales.core.services;


import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import fr.abes.licencesnationales.core.repository.ip.IpRepository;
import fr.abes.licencesnationales.core.repository.ip.IpV4Repository;
import fr.abes.licencesnationales.core.repository.ip.IpV6Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;


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
    private EtablissementRepository etablissementRepository;

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
            if (candidate.getIpRange().contains(ip.getIpRange())) {
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
            if (candidate.getIpRange().contains(ip.getIpRange())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ajouter une IP
     * @param ip transitoire (sans id) à enregistrer
     * @return ip avec un id
     */
    public IpEntity save(IpEntity ip) {
        return ipRepository.save(ip);
    }

}
