package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpModifieeDTO {

    //pas besoin dans le cas modif par etab (on le cherche dans le fr.abes.licencesnationales.security contextuser
    //mais necessaire dans le cas d'une modif par l'admin (puisque dans le fr.abes.licencesnationales.security context c'est le token admin
    @NotBlank
    private String siren;
    @NotBlank
    private String id;
    @NotBlank(message="L'IP est obligatoire")
    private String ip;
    @NotBlank
    private String validee;
    @NotBlank(message="Le type d'acces est obligatoire")
    private String typeAcces;
    @NotBlank(message="Le type d'IP est obligatoire")
    private String typeIp;
    private String commentaires;

}
