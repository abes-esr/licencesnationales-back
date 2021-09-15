package fr.abes.licencesnationales.core.entities.editeur.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class EditeurCreeEventEntity extends EditeurEventEntity {


    public EditeurCreeEventEntity(Object source, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies,
                              String adresseEditeur, Set<ContactCommercialEditeurEntity> listeContactCommercialEditeur,
                              Set<ContactTechniqueEditeurEntity> listeContactTechniqueEditeur) throws JsonProcessingException {
        super(source);
        this.event = "editeurCree";
        this.dateCreationEvent = new Date();
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies =groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        this.listeContactCommercialEditeur = mapper.writeValueAsString(listeContactCommercialEditeur);
        this.listeContactTechniqueEditeur = mapper.writeValueAsString(listeContactTechniqueEditeur);
    }
}
