package fr.abes.lnevent.dto.etablissement;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
/*

@Getter
public class EtablissementModifieDTO extends EtablissementDTO{

    private Long id;

    private Long idContact;

    public EtablissementModifieDTO(String nom, String siren, String typeEtablissement, String idAbes, String mailContact, String motDePasse, String nomContact, String prenomContact, String telephoneContact, String adresseContact, String boitePostaleContact, String codePostalContact, String cedexContact, String villeContact, String roleContact) {
        super(nom, siren, typeEtablissement, idAbes, mailContact, motDePasse, nomContact, prenomContact, telephoneContact, adresseContact, boitePostaleContact, codePostalContact, cedexContact, villeContact, roleContact);
    }
}
*/

@Getter
public class EtablissementModifieDTO {

    @NotBlank
    @Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir 9 chiffres")
    private String siren;

    @NotBlank
    private String nomContact;

    @NotBlank
    private String adresseContact;

    @NotBlank
    private String mailContact;

    @NotBlank
    private String telephoneContact;


    public EtablissementModifieDTO(String siren, String nomContact, String adresseContact, String mailContact, String telephoneContact) {
        this.siren = siren;
        this.nomContact = nomContact;
        this.adresseContact = adresseContact;
        this.mailContact = mailContact;
        this.telephoneContact = telephoneContact;
    }
}
