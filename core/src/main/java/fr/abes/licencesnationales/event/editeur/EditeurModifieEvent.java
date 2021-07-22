package fr.abes.licencesnationales.event.editeur;


import fr.abes.licencesnationales.dto.editeur.ContactCommercialEditeurDTO;
import fr.abes.licencesnationales.dto.editeur.ContactTechniqueEditeurDTO;
import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class EditeurModifieEvent extends Event {

    private Long id;

    private String nomEditeur;

    private String identifiantEditeur;

    private List<String> groupesEtabRelies;

    private String adresseEditeur;

    private Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTOS;

    private Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTOS;

    public EditeurModifieEvent(Object source,
                            String nomEditeur,
                            String identifiantEditeur,
                            List<String> groupesEtabRelies,
                            String adresseEditeur,
                            Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTOS,
                            Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTOS) {
        super(source);
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.groupesEtabRelies = groupesEtabRelies;
        this.adresseEditeur = adresseEditeur;
        //this.mailsPourBatch = mailsPourBatch;
        //this.mailPourInformation = mailPourInformation;
        this.contactCommercialEditeurDTOS = contactCommercialEditeurDTOS;
        this.contactTechniqueEditeurDTOS = contactTechniqueEditeurDTOS;
    }


    /*public EditeurModifieEvent(
            Object source,
            String nomEditeur,
            String identifiantEditeur,
            String adresseEditeur,
            Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTO,
            Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTO) {

        super(source);
    }*/
}
