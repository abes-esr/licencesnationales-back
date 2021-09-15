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
public class EditeurModifieEvent extends Event {
    private Integer id;
    private String nomEditeur;
    private String identifiantEditeur;
    private List<String> groupesEtabRelies;
    private String adresseEditeur;
    public Set<ContactCommercialEditeurEntity> listeContactCommercialEditeur;
    public Set<ContactTechniqueEditeurEntity> listeContactTechniqueEditeur;

    public EditeurModifieEvent(Object source) {
        super(source);
        this.groupesEtabRelies = new ArrayList<>();
        this.listeContactTechniqueEditeur = new HashSet<>();
        this.listeContactCommercialEditeur = new HashSet<>();
    }

    public EditeurModifieEvent(Object source,
                            Integer id, String nomEditeur, String identifiantEditeur,
                               List<String> groupesEtabRelies, String adresseEditeur,
                               Set<ContactCommercialEditeurEntity> contactCommercialEditeur,
                               Set<ContactTechniqueEditeurEntity> contactTechniqueEditeur) {
        super(source);
        this.id = id;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies = groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        this.listeContactCommercialEditeur = contactCommercialEditeur;
        this.listeContactTechniqueEditeur = contactTechniqueEditeur;
    }

}
