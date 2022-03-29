package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementHistoriqueDto {

    @JsonProperty("event")
    private String event;

    @JsonProperty("date")
    private String dateCreationEvent;

    @JsonProperty("Etablissement")
    private String nomEtab;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("Mail")
    private String mail;

    @JsonProperty("Nom")
    private String nomContact;

    @JsonProperty("Prenom")
    private String prenomContact;

    @JsonProperty("Telephone")
    private String telephoneContact;

    @JsonProperty("Adresse")
    private String adresseContact;

    @JsonProperty("BP")
    private String boitePostaleContact;

    @JsonProperty("CP")
    private String codePostalContact;

    @JsonProperty("CEDEX")
    private String cedexContact;

    @JsonProperty("ancienSiren")
    private String ancienSiren;

    @JsonProperty("etablissementsDivise")
    private String etablissementsDivise;

    @JsonProperty("anciensEtablissements")
    private String anciensEtablissements;


}
