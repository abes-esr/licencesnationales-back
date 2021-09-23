package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un exemplaire de Notice au format JSON de l'API
 */
@Getter @Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", defaultImpl = java.lang.Void.class)
@JsonSubTypes({@JsonSubTypes.Type(value = EtablissementModifieAdminWebDto.class, name = "admin"),
        @JsonSubTypes.Type(value = EtablissementModifieUserWebDto.class, name = "etab")})
public class EtablissementWebDto {
    @JsonProperty("contact")
    private ContactWebDto contact;
}
