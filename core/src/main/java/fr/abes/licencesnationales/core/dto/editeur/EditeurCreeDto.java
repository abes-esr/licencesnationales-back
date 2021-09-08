package fr.abes.licencesnationales.core.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurCreeDto {
    private Long idEditeur;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    private Set<ContactCommercialEditeurDto> listeContactCommercialEditeurDto = new HashSet<>();
    private Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeurDto = new HashSet<>();
    private Date dateCreation = new Date();
}
