package fr.abes.lnevent.dto.ip;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class IpSupprimeeDTO {

    @NotBlank
    private String id;
    @NotBlank
    private String siren;
}
