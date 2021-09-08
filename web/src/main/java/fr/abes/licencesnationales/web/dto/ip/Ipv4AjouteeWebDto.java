package fr.abes.licencesnationales.web.dto.ip;

import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
public class Ipv4AjouteeWebDto extends IpAjouteeDto {
    @NotBlank(message="L'IP est obligatoire")
    @Pattern(regexp = "^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$", message = "L'IP fournie n'est pas valide")
    private String ip;
    private String siren;
    private String typeIp;
    private String typeAcces;
    private String commentaires;


    public Ipv4AjouteeWebDto(String typeIp, String ip, String typeAcces, String commentaires) {
        this.typeIp = typeIp;
        this.ip = ip;
        this.typeAcces = typeAcces;
        this.commentaires = commentaires;
    }
}
