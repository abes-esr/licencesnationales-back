package fr.abes.licencesnationales.core.event.editeur;


import fr.abes.licencesnationales.core.dto.contact.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.contact.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class EditeurModifieEvent extends Event {
    private Long id;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    public Set<ContactCommercialEditeurDto> listeContactCommercialEditeur;
    public Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeur;

    public EditeurModifieEvent(Object source) {
        super(source);
        this.listeContactTechniqueEditeur = new HashSet<>();
        this.listeContactCommercialEditeur = new HashSet<>();
    }

    public EditeurModifieEvent(Object source,
                            Long id, String nomEditeur, String identifiantEditeur,
                               List<String> groupesEtabRelies, String adresseEditeur,
                               Set<ContactCommercialEditeurDto> contactCommercialEditeurDtos,
                               Set<ContactTechniqueEditeurDto> contactTechniqueEditeurDtos) {
        super(source);
        this.id = id;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies = groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        this.listeContactCommercialEditeur = contactCommercialEditeurDtos;
        this.listeContactTechniqueEditeur = contactTechniqueEditeurDtos;
    }

}
