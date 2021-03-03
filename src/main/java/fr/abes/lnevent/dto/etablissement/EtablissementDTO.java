package fr.abes.lnevent.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EtablissementDTO {

    private String nom;

    private String siren;

    private String typeEtablissement;

    private String idAbes;

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
