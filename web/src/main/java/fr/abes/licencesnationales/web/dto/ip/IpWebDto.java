package fr.abes.licencesnationales.web.dto.ip;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpWebDto {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("statut")
    private String statut;
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

    public IpWebDto(Integer id, String ip, String statut, Date dateCreation, Date dateModification, String commentaires) {
        this.id = id;
        this.ip = ip;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.commentaires = commentaires;
    }
}
