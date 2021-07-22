package fr.abes.licencesnationales.dto.etablissement;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class EtablissementModifieDTO {

    @NotBlank
    @Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir 9 chiffres")
    private String siren;
    @NotBlank
    private String nomContact;
    @NotBlank
    @Pattern(regexp = "^([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+([-]([A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+(( |')[A-Za-zàáâäçèéêëìíîïñòóôöùúûü]+)*)+)*$", message = "Le prénom fourni n'est pas valide")
    private String prenomContact;
    @NotBlank
    private String mailContact;
    @NotBlank
    private String telephoneContact;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresseContact;

    private String boitePostaleContact;

    @NotBlank
    @Pattern(regexp = "^\\d{5}$", message = "Le code postal fourni n'est pas valide")
    private String codePostalContact;

    @NotBlank
    @Pattern(regexp = "^([a-zA-Z\\u0080-\\u024F]+(?:. |-| |'))*[a-zA-Z\\u0080-\\u024F]*$", message = "La ville fournie n'est pas valide")
    private String villeContact;

    private String cedexContact;

    public EtablissementModifieDTO(String siren, String nomContact, String prenomContact, String mailContact, String telephoneContact, String adresseContact, String boitePostaleContact, String codePostalContact, String villeContact, String cedexContact) {
        this.siren = siren;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
        this.telephoneContact = telephoneContact;
        this.adresseContact = adresseContact;
        this.boitePostaleContact = boitePostaleContact;
        this.codePostalContact = codePostalContact;
        this.villeContact = villeContact;
        this.cedexContact = cedexContact;
    }
}
