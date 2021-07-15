package fr.abes.lnevent.event.editeur;

import fr.abes.lnevent.controllers.EditeurController;
import fr.abes.lnevent.dto.editeur.ContactCommercialEditeurDTO;
import fr.abes.lnevent.dto.editeur.ContactTechniqueEditeurDTO;
import fr.abes.lnevent.entities.ContactCommercialEditeurEntity;
import fr.abes.lnevent.entities.ContactTechniqueEditeurEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.Event;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class EditeurModifieEvent extends Event {

    /*private Long id;

    private String nomEditeur;

    private String identifiantEditeur;

    private String adresseEditeur;

    private Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities;

    private Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities;

    public EditeurModifieEvent(Object source,
                            String nomEditeur,
                            String identifiantEditeur,
                            String adresseEditeur, *//*List<String> mailsPourBatch, List<String> mailPourInformation,Set<EtablissementEntity> etablissements,*//*
                            Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities,
                            Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities) {
        super(source);
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        //this.mailsPourBatch = mailsPourBatch;
        //this.mailPourInformation = mailPourInformation;
        this.contactCommercialEditeurEntities = contactCommercialEditeurEntities;
        this.contactTechniqueEditeurEntities = contactTechniqueEditeurEntities;
    }
*/

    public EditeurModifieEvent(
            Object source,
            String nomEditeur,
            String identifiantEditeur,
            String adresseEditeur,
            Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTO,
            Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTO) {

        super(source);
    }
}
