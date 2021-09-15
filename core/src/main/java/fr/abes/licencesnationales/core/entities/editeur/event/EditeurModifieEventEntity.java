package fr.abes.licencesnationales.core.entities.editeur.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class EditeurModifieEventEntity extends EditeurEventEntity {


    public EditeurModifieEventEntity(Object source, Integer id, String nomEditeur, String identifiantEditeur,
                                     List<String> groupesEtabRelies, String adresseEditeur,
                                     Set<ContactCommercialEditeurEntity> contactCommercialEditeur,
                                     Set<ContactTechniqueEditeurEntity> contactTechniqueEditeur) throws JsonProcessingException {
        super(source);
        this.event = "editeurModifie";
        this.dateCreationEvent = new Date();
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies =groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        this.listeContactCommercialEditeur = mapper.writeValueAsString(contactCommercialEditeur);
        this.listeContactTechniqueEditeur = mapper.writeValueAsString(contactTechniqueEditeur);
    }
}
