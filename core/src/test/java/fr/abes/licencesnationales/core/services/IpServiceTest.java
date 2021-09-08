package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.entities.IpEntity;
import org.junit.jupiter.api.Test;

class IpServiceTest {

    @Test
    void checkDoublonIp() {
        IpEntity ipEntity = new IpEntity();
        ipEntity.setTypeIp("IPV4");
        ipEntity.setTypeAcces("ip");
        ipEntity.setIp("192.168.1.1-192.168.1.50");

    }
}