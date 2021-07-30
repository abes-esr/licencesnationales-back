package fr.abes.licencesnationales.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurFusionneDto {
    private List<Long> idEditeurFusionnes;

    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    private Set<ContactCommercialEditeurDto> listeContactCommercialEditeurDto = new HashSet<>();
    private Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeurDto = new HashSet<>();
}
