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

    @JsonProperty("nomEtab")
    private String nomEtab;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("mail")
    private String mail;

    @JsonProperty("mailContact")
    private String mailContact;

    @JsonProperty("nomContact")
    private String nomContact;

    @JsonProperty("prenomContact")
    private String prenomContact;

    @JsonProperty("telephoneContact")
    private String telephoneContact;

    @JsonProperty("adresseContact")
    private String adresseContact;

    @JsonProperty("boitePostaleContact")
    private String boitePostaleContact;

    @JsonProperty("codePostalContact")
    private String codePostalContact;

    @JsonProperty("cedexContact")
    private String cedexContact;

    @JsonProperty("ancienSiren")
    private String ancienSiren;

    @JsonProperty("etablissementsDivise")
    private String etablissementsDivise;

    @JsonProperty("anciensEtablissements")
    private String anciensEtablissements;


}
