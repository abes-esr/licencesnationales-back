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
public class Ipv4ModifieeDTO extends IpModifieeDTO {

    @NotBlank(message="L'IP est obligatoire")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$", message = "L'IP fournie n'est pas valide")
    private String ip;

    public Ipv4ModifieeDTO(String siren, String id, String ip, String validee, String typeIp,String typeAcces, String commentaires) {
        super(siren, id, ip, validee, typeAcces, typeIp, commentaires);
        this.ip = ip;
    }
}
