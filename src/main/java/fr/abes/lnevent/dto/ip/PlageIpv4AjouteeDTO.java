package fr.abes.lnevent.dto.ip;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class PlageIpv4AjouteeDTO extends IpAjouteeDTO {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))?\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))?$", message = "La plage d'Ips fournie n'est pas valide")
    private String ip;

    public PlageIpv4AjouteeDTO(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
        this.ip = ip;
    }
}
