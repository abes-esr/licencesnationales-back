package fr.abes.licencesnationales.dto.editeur;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class EditeurCreeDTO extends EditeurDTO{

    public EditeurCreeDTO(String nomEditeur,
                          String identifiantEditeur,
                          List<String> groupesEtabRelies,
                          String adresseEditeur,
                          Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTO,
                          Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTO){

        super(  nomEditeur,
                identifiantEditeur,
                groupesEtabRelies,
                adresseEditeur,
                contactCommercialEditeurDTO,
                contactTechniqueEditeurDTO);
    }
}
