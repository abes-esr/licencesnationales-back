package fr.abes.licencesnationales.web.dto.ip;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpHistoriqueDto {

    @JsonProperty("event")
    private String event;

    @JsonProperty("date")
    private String dateCreationEvent;

    @JsonProperty("IP")
    private String IP;

    @JsonProperty("Commentaire")
    private String commentaires;

    @JsonProperty("CommentaireAdmin")
    private String commentaire;

}
