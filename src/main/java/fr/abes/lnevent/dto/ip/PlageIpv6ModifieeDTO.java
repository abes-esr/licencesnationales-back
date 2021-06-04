package fr.abes.lnevent.dto.ip;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class PlageIpv6ModifieeDTO extends IpModifieeDTO {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^\\s*((([0-9a-fA-F]{1,4}:){6,6}[0-9a-fA-F]{1,4}-[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}-[0-9a-fA-F]{1,4}))\\s*$", message = "La plage d'Ips fournie n'est pas valide")
    private String ip;

    public PlageIpv6ModifieeDTO(String siren, String id, String ip, String validee, String typeAcces, String typeIp, String commentaires) {
        super(siren, id, ip, validee, typeAcces, typeIp, commentaires);
        this.ip = ip;
    }
}
