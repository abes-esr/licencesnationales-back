package fr.abes.licencesnationales.web.dto.editeur;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurCreeWebDto  {

    @JsonProperty("nomEditeur")
    private String nomEditeur;

    @JsonProperty("identifiantEditeur")
    private String identifiantEditeur;

    @JsonProperty("groupesEtabRelies")
    private List<String> groupesEtabRelies;

    @JsonProperty("adresseEditeur")
    private String adresseEditeur;

    @JsonProperty("listeContactCommercialEditeurDTO")
    public Set<ContactCommercialEditeurWebDto> listeContactCommercialEditeur;

    @JsonProperty("listeContactTechniqueEditeurDTO")
    public Set<ContactTechniqueEditeurWebDto> listeContactTechniqueEditeur;
}
