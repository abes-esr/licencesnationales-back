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
public class EditeurFusionneEvent extends Event {
    private List<Long> idEditeurFusionnes;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    private Set<ContactCommercialEditeurDto> listeContactCommercialEditeur;
    private Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeur;

    public EditeurFusionneEvent(Object source) {
        super(source);
        this.listeContactCommercialEditeur = new HashSet<>();
        this.listeContactTechniqueEditeur = new HashSet<>();
    }

    public EditeurFusionneEvent(Object source, List<Long> idEditeurFusionnes, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies,
                                String adresseEditeur, Set<ContactCommercialEditeurDto> listeContactCommercialEditeur,
                                Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeur) {
        super(source);
        this.idEditeurFusionnes = idEditeurFusionnes;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies = groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        this.listeContactCommercialEditeur = listeContactCommercialEditeur;
        this.listeContactTechniqueEditeur = listeContactTechniqueEditeur;
    }
}
