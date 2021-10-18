package fr.abes.licencesnationales.web.dto.etablissement.modification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", defaultImpl = java.lang.Void.class)
@JsonSubTypes({@JsonSubTypes.Type(value = EtablissementModifieAdminWebDto.class, name = "admin"),
                @JsonSubTypes.Type(value = EtablissementModifieUserWebDto.class, name = "etab")})
public abstract class EtablissementModifieWebDto {
    @JsonProperty("siren")
    protected String siren;
    @JsonProperty("contact")
    protected ContactModifieWebDto contact;
}
