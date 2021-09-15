package fr.abes.licencesnationales.web.dto.ip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class IpWebDto {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("validee")
    private boolean validee;
    @JsonProperty("dateCreation")
    private Date dateCreation;
    @JsonProperty("dateModification")
    private Date dateModification;
    @JsonProperty("typeAcces")
    private String typeAcces;
    @JsonProperty("typeIp")
    private String typeIp;
    @JsonProperty("commentaires")
    private String commentaires;
}
