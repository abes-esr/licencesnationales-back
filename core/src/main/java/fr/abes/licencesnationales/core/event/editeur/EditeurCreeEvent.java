package fr.abes.licencesnationales.core.event.editeur;


import fr.abes.licencesnationales.core.dto.contact.ContactCommercialEditeurDto;
import fr.abes.licencesnationales.core.dto.contact.ContactTechniqueEditeurDto;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class EditeurCreeEvent extends Event {
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    private Set<ContactCommercialEditeurDto> listeContactCommercialEditeur = new HashSet<>();
    private Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeur = new HashSet<>();
    private Date dateCreation = new Date();

    public EditeurCreeEvent(Object source) {
        super(source);
    }

    public EditeurCreeEvent(Object source, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies,
                            String adresseEditeur, Set<ContactCommercialEditeurDto> listeContactCommercialEditeurDto,
                            Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeurDto) {
        super(source);
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies = groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        this.listeContactCommercialEditeur = listeContactCommercialEditeurDto;
        this.listeContactTechniqueEditeur = listeContactTechniqueEditeurDto;
        this.dateCreation = new Date();
    }
}
