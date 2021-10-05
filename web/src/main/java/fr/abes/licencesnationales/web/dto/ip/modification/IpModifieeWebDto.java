package fr.abes.licencesnationales.web.dto.ip.modification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "role", defaultImpl = java.lang.String.class)
@JsonSubTypes({@JsonSubTypes.Type(value = IpModifieeAdminWebDto.class, name = "admin"),
        @JsonSubTypes.Type(value = IpModifieeUserWebDto.class, name = "etab")})
public abstract class IpModifieeWebDto {
    protected String ip;
    protected String commentaires;
    protected String typeIp;

}
