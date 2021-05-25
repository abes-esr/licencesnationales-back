package fr.abes.lnevent.dto.ip;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class PlageIpv4ModifieeDTO extends IpModifieeDTO {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))?\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))?$", message = "La plage d'Ips fournie n'est pas valide")
    private String ip;

    public PlageIpv4ModifieeDTO(String siren, String id, String ip, String validee, String typeIp, String typeAcces, String commentaires) {
        super(siren, id, ip, validee, typeAcces, typeIp, commentaires);
        this.ip = ip;
    }
}
