package fr.abes.licencesnationales.web.dto.etablissement;

import lombok.Getter;

@Getter
public class EtablissementModifieWebDto {
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
