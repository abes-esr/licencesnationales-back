package fr.abes.licencesnationales.core.event.editeur;


import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
    private Set<ContactCommercialEditeurEntity> listeContactCommercialEditeur;
    private Set<ContactTechniqueEditeurEntity> listeContactTechniqueEditeur;

    public EditeurFusionneEvent(Object source) {
        super(source);
        this.listeContactCommercialEditeur = new HashSet<>();
        this.listeContactTechniqueEditeur = new HashSet<>();
        this.groupesEtabRelies = new ArrayList<>();
    }

    public EditeurFusionneEvent(Object source, List<Long> idEditeurFusionnes, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies,
                                String adresseEditeur, Set<ContactCommercialEditeurEntity> listeContactCommercialEditeur,
                                Set<ContactTechniqueEditeurEntity> listeContactTechniqueEditeur) {
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
