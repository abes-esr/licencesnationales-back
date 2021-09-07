package fr.abes.licencesnationales.dto.etablissement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class EtablissementDto {
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


}
