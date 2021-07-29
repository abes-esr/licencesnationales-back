package fr.abes.licencesnationales.dto.editeur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditeurModifieDto {
    private Long id;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    public Set<ContactCommercialEditeurEventDto> listeContactCommercialEditeurEventDto = new HashSet<>();
    public Set<ContactTechniqueEditeurEventDto> listeContactTechniqueEditeurEventDto = new HashSet<>();

}
