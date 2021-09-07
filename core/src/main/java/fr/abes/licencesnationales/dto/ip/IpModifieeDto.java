package fr.abes.licencesnationales.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpModifieeDto {

    //pas besoin dans le cas modif par etab (on le cherche dans le fr.abes.licencesnationales.web.security contextuser
    //mais necessaire dans le cas d'une modif par l'admin (puisque dans le fr.abes.licencesnationales.web.security context c'est le token admin
    private String siren;
    private Long id;
    private String ip;
    private boolean validee;
    private String typeAcces;
    private String typeIp;
    private String commentaires;

}
