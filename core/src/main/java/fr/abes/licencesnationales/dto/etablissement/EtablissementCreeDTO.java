package fr.abes.licencesnationales.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtablissementCreeDTO extends EtablissementDTO {

    private String recaptcha;

    public EtablissementCreeDTO(String nom, String siren, String typeEtablissement, String idAbes, String nomContact, String prenomContact, String adresseContact, String boitePostaleContact, String codePostalContact, String villeContact, String cedexContact, String telephoneContact, String mailContact, String motDePasse, String roleContact, String recaptcha) {
        super(nom, siren, typeEtablissement, idAbes,  nomContact, prenomContact, adresseContact, boitePostaleContact, codePostalContact, villeContact, cedexContact, telephoneContact, mailContact, motDePasse, roleContact);
        this.recaptcha = recaptcha;
    }


}
