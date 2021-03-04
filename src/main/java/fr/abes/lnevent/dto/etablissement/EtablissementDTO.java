package fr.abes.lnevent.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@AllArgsConstructor
public class EtablissementDTO {

    @NotBlank
    @Size(min = 3, max = 20)
    private String nom;

    private String siren;

    private String typeEtablissement;

    private String idAbes;

    @NotBlank
    @Size(max = 50)
    @Email
    private String mailContact;

    private String motDePasse;

    private String nomContact;

    private String prenomContact;

    private String telephoneContact;

    private String adresseContact;

    private String boitePostaleContact;

    private String codePostalContact;

    private String cedexContact;

    private String villeContact;

    private String roleContact;

}
