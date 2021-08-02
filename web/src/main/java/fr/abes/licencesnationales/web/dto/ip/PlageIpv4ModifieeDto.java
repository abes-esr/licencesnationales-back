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
public class PlageIpv4ModifieeDto extends IpModifieeDto {

    @NotBlank(message="La plage d'Ips est obligatoire")
    @Pattern(regexp = "^(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})?\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))\\.(([(\\d+)(x+)]){1,3})(-+([(\\d+)(x)]{1,3}))$", message = "La plage d'Ips fournie n'est pas valide")
    private String ip;

}
