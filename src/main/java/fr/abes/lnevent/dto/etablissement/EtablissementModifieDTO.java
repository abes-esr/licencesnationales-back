package fr.abes.lnevent.dto.etablissement;

import lombok.Getter;

@Getter
public class EtablissementModifieDTO extends EtablissementDTO{

    private Long id;

    private Long idContact;

    public EtablissementModifieDTO(String nom, String siren, String typeEtablissement, String idAbes, String mailContact, String motDePasse, String nomContact, String prenomContact, String telephoneContact, String adresseContact, String boitePostaleContact, String codePostalContact, String cedexContact, String villeContact, String roleContact) {
        super(nom, siren, typeEtablissement, idAbes, mailContact, motDePasse, nomContact, prenomContact, telephoneContact, adresseContact, boitePostaleContact, codePostalContact, cedexContact, villeContact, roleContact);
    }
}
