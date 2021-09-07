package fr.abes.licencesnationales.services;

import fr.abes.licencesnationales.entities.IpEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpServiceTest {

    @Test
    void checkDoublonIp() {
        IpEntity ipEntity = new IpEntity();
        ipEntity.setTypeIp("IPV4");
        ipEntity.setTypeAcces("ip");
        ipEntity.setIp("192.168.1.1-192.168.1.50");

    }
}