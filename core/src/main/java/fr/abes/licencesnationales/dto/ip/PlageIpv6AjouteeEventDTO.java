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
public class PlageIpv6AjouteeEventDTO extends IpAjouteeEventDTO {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^\\s*((([0-9a-fA-F]{1,4}:){6,6}[0-9a-fA-F]{1,4}-[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}-[0-9a-fA-F]{1,4}))\\s*$", message = "La plage d'Ips fournie n'est pas valide\" //regex qui filtre le texte parasite au cas où : cf https://stackoverflow.com/a/53442371\n" +
            "      ],")
    private String ip;

    public PlageIpv6AjouteeEventDTO(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
        this.ip = ip;
    }
}
