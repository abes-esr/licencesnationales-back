package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlageIpv6ModifieeDTO extends IpModifieeDTO {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^\\s*((([0-9a-fA-F]{1,4}:){6,6}[0-9a-fA-F]{1,4}-[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}-[0-9a-fA-F]{1,4}))\\s*$", message = "La plage d'Ips fournie n'est pas valide")
    private String ip;

    public PlageIpv6ModifieeDTO(String siren, String id, String ip, String validee, String typeAcces, String typeIp, String commentaires) {
        super(siren, id, ip, validee, typeAcces, typeIp, commentaires);
        this.ip = ip;
    }
}
