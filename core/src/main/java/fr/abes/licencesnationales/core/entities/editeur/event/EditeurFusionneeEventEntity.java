package fr.abes.licencesnationales.core.entities.editeur.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class EditeurFusionneeEventEntity extends EditeurEventEntity {


    public EditeurFusionneeEventEntity(Object source, List<Integer> idEditeurFusionnes, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies,
                                       String adresseEditeur, Set<ContactCommercialEditeurEntity> listeContactCommercialEditeur,
                                       Set<ContactTechniqueEditeurEntity> listeContactTechniqueEditeur) throws JsonProcessingException {
        super(source);
        this.event = "editeurFusionne";
        this.dateCreationEvent = new Date();
        this.nomEditeur = nomEditeur;
        this.adresseEditeur = adresseEditeur;
        this.idEditeurFusionnes = idEditeurFusionnes;
    }
}
