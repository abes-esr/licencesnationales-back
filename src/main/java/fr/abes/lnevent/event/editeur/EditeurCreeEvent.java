package fr.abes.lnevent.event.editeur;

import fr.abes.lnevent.controllers.EditeurController;
import fr.abes.lnevent.dto.editeur.ContactCommercialEditeurDTO;
import fr.abes.lnevent.dto.editeur.ContactTechniqueEditeurDTO;
import fr.abes.lnevent.dto.editeur.EditeurDTO;
import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.entities.ContactCommercialEditeurEntity;
import fr.abes.lnevent.entities.ContactTechniqueEditeurEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.event.Event;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

@Getter
public class EditeurCreeEvent extends Event {


   /* private String nomEditeur;

    private String identifiantEditeur;

    private String adresseEditeur;

    private Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTOS;

    private Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTOS;

    public EditeurCreeEvent(Object source,
                            String nomEditeur,
                            String identifiantEditeur,
                            String adresseEditeur, *//*List<String> mailsPourBatch, List<String> mailPourInformation, Set<EtablissementEntity> etablissements,*//*
                            Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTOS,
                            Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTOS) {
        super(source);
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        //this.mailsPourBatch = mailsPourBatch;
        //this.mailPourInformation = mailPourInformation;
        this.contactCommercialEditeurDTOS = contactCommercialEditeurDTOS;
        this.contactTechniqueEditeurDTOS = contactTechniqueEditeurDTOS;
    }
*/

    private EditeurDTO editeurDTO;

    public EditeurCreeEvent(Object source, EditeurDTO editeurDTO) {
        super(source);
        this.editeurDTO = editeurDTO;
    }

  /*  public EditeurCreeEvent(Object source, String nomEditeur, String identifiantEditeur, String adresseEditeur, ContactCommercialEditeurDTO contactCommercialEditeurDTO, ContactTechniqueEditeurDTO contactTechniqueEditeurDTO) {
        super(source);
        this.
    }*/
}
