package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", defaultImpl = java.lang.Void.class)
@JsonSubTypes({@JsonSubTypes.Type(value = EtablissementAdminWebDto.class, name = "admin"),
        @JsonSubTypes.Type(value = EtablissementUserWebDto.class, name = "etab")})
public class EtablissementWebDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("nom")
    private String name;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("typeEtablissement")
    private String typeEtablissement;

    @JsonProperty("idAbes")
    private String idAbes;

    @JsonProperty("statut")
    private String statut;

    @JsonProperty("statutIps")
    private String statutIps;

    @JsonProperty("contact")
    private ContactWebDto contact;
    @JsonProperty("dateCreation")
    private String dateCreation;
}
