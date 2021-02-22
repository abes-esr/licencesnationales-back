package fr.abes.lnevent.dto.etablissement;

import lombok.Getter;

@Getter
public class EtablissementModifieDTO {

    private Long id;

    private String nom;

    private String adresse;

    private String siren;

    private String typeEtablissement;

    private String motDePasse;

    private String idAbes;

    private String idContact;

    private String mailContact;

    private String nomContact;

    private String prenomContact;

    private String telephoneContact;

    private String adresseContact;
}
