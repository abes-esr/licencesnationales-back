package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpAjouteeDTO {


    @NotBlank
    private String siren;
    @NotBlank(message="Le type d'IP est obligatoire")
    private String typeIp;
    @NotBlank(message="L'IP est obligatoire")
    private String ip;
    @NotBlank
    private String typeAcces;
    private String commentaires;


}
