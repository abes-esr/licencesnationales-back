package fr.abes.licencesnationales.web.dto.ip;

import fr.abes.licencesnationales.core.dto.ip.IpAjouteeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlageIpv4AjouteeWebDto extends IpAjouteeDto {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))$", message = "La plage d'Ips fournie n'est pas valide")
    private String ip;

    public PlageIpv4AjouteeWebDto(String siren, String typeIp, String ip, String typeAcces, String commentaires) {
        super(siren, typeIp, ip, typeAcces, commentaires);
        this.ip = ip;
    }
}
