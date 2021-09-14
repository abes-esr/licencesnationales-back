package fr.abes.licencesnationales.web.dto.etablissement;

import fr.abes.licencesnationales.web.dto.ContactWebDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementCreeWebDto {
    private String name;
    private String siren;
    private String typeEtablissement;
    private String motDePasse;
    private String idAbes;
    private ContactWebDto contact;
    private String roleContact;
    private String recaptcha;
}
