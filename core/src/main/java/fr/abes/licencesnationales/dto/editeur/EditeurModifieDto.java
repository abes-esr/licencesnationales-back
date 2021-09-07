package fr.abes.licencesnationales.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurModifieDto {
    private Long id;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    public Set<ContactCommercialEditeurDto> listeContactCommercialEditeurDto = new HashSet<>();
    public Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeurDto = new HashSet<>();

}
