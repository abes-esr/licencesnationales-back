package fr.abes.licencesnationales.web.dto.etablissement;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class EtablissementFusionneWebDto {
    private String nom;
    private String siren;
    private String typeEtablissement;
    private String idAbes;
    private String nomContact;
    private String prenomContact;
    private String adresseContact;
    private String boitePostaleContact;
    private String codePostalContact;
    private String villeContact;
    private String cedexContact;
    private String telephoneContact;
    private String mailContact;
    private String motDePasse;
    private String roleContact;
    private ArrayList<String> sirenFusionnes;
}
