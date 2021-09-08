package fr.abes.licencesnationales.core.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EtablissementModifieDto {
    private String siren;
    private String nomContact;
    private String prenomContact;
    private String mailContact;
    private String telephoneContact;
    private String adresseContact;
    private String boitePostaleContact;
    private String codePostalContact;
    private String villeContact;
    private String cedexContact;
}
