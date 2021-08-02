package fr.abes.licencesnationales.web.dto.ip;

import fr.abes.licencesnationales.core.dto.ip.IpModifieeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ipv4ModifieeDto extends IpModifieeDto {

    @NotBlank(message="L'IP est obligatoire")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$", message = "L'IP fournie n'est pas valide")
    private String ip;

    public Ipv4ModifieeDto(String siren, String id, String ip, String validee, String typeIp, String typeAcces, String commentaires) {
        super(siren, id, ip, validee, typeAcces, typeIp, commentaires);
        this.ip = ip;
    }
}
