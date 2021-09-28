package fr.abes.licencesnationales.web.dto.ip.cree;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, property = "typeIp", defaultImpl = java.lang.String.class)
@JsonSubTypes({@JsonSubTypes.Type(value = Ipv4AjouteeWebDto.class, name = "IPV4"),
        @JsonSubTypes.Type(value = Ipv6AjouteeWebDto.class, name = "IPV6")})
public abstract class IpAjouteeWebDto {
    @JsonProperty("ip")
    protected String ip;
    @JsonProperty("commentaires")
    protected String commentaires;
}
