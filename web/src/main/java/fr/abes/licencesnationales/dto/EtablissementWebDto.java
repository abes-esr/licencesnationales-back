package fr.abes.licencesnationales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Représente un exemplaire de Notice au format JSON de l'API
 */
@Getter @Setter
public class EtablissementWebDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nomEtab")
    private String name;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("typeEtablissement")
    private String typeEtablissement;

    @JsonProperty("statut")
    private boolean valide;

    @JsonProperty("idAbes")
    private String idAbes;

    @JsonProperty("contact")
    private ContactWebDto contact;

}
