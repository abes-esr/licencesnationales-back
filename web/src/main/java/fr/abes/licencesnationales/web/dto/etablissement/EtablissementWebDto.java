package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieAdminWebDto;
import fr.abes.licencesnationales.web.dto.etablissement.modification.EtablissementModifieUserWebDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", defaultImpl = java.lang.Void.class)
@JsonSubTypes({@JsonSubTypes.Type(value = EtablissementAdminWebDto.class, name = "admin"),
        @JsonSubTypes.Type(value = EtablissementUserWebDto.class, name = "etab")})
public class EtablissementWebDto {
    @JsonProperty("contact")
    private ContactWebDto contact;
}
