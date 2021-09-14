package fr.abes.licencesnationales.web.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurModifieWebDto {
    private Long idEditeur;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    public Set<ContactCommercialEditeurWebDto> listeContactCommercialEditeur;
    public Set<ContactTechniqueEditeurWebDto> listeContactTechniqueEditeur;


}
