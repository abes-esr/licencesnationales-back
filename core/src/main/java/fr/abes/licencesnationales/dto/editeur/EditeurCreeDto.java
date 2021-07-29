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
public class EditeurCreeDto {
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    private Set<ContactCommercialEditeurEventDto> listeContactCommercialEditeurEventDto = new HashSet<>();
    private Set<ContactTechniqueEditeurEventDto> listeContactTechniqueEditeurEventDto = new HashSet<>();
}
