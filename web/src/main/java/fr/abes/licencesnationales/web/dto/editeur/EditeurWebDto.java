package fr.abes.licencesnationales.web.dto.editeur;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditeurWebDto {
    @JsonProperty("nomEditeur")
    private String nomEditeur;

    @JsonProperty("identifiantEditeur")
    private String identifiantEditeur;

    @JsonProperty("groupesEtabRelies")
    private List<String> groupesEtabRelies;

    @JsonProperty("adresseEditeur")
    private String adresseEditeur;

    @JsonProperty("contactCommercial")
    public Set<ContactEditeurWebDto> listeContactCommercialEditeurWebDto;

    @JsonProperty("contactTechnique")
    public Set<ContactEditeurWebDto> listeContactEditeurWebDto;

}
