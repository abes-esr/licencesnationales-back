package fr.abes.licencesnationales.batch.relance.dto;

import fr.abes.licencesnationales.core.entities.ip.IpEntity;

public class IpDto {
    private Integer id;
    private String ip;

    public IpDto(IpEntity ip) {
        this.id = ip.getId();
        this.ip = ip.getIp();
    }
}
