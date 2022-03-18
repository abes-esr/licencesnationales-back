package fr.abes.licencesnationales.web.dto.etablissement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EtablissementSearchWebDto {
    private Integer id;
    private String nomEtab;
    private String siren;
    private String idAbes;
    private String nomContact;
    private String prenomContact;
    private String mailContact;
    private String adresseContact;
    private String villeContact;
    private String CpContact;
}
