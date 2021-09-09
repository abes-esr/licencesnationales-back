package fr.abes.licencesnationales.core.services;


import com.github.jgonian.ipmath.Ipv4Range;
import com.github.jgonian.ipmath.Ipv6Range;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.repository.EtablissementRepository;
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

    public boolean isIpAlreadyExists(Ipv4Range ip) {

        List<IpV4> all = ipV4Repository.findAll();

        Iterator<IpV4> iter = all.iterator();
        while (iter.hasNext()) {

            IpV4 candidate = iter.next();
            if (candidate.getIpRange().contains(ip)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIpAlreadyExists(Ipv6Range ip) {

        List<IpV6> all = ipV6Repository.findAll();

        Iterator<IpV6> iter = all.iterator();
        while (iter.hasNext()) {

            IpV6 candidate = iter.next();
            if (candidate.getIpRange().contains(ip)) {
                return true;
            }
        }
        return false;
    }
}
