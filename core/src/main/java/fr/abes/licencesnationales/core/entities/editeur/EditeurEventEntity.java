package fr.abes.licencesnationales.core.entities.editeur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurSupprimeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "EditeurEvent")
@NoArgsConstructor @Getter
public class EditeurEventEntity implements Serializable {
    @Autowired
    @Transient
    private ObjectMapper mapper;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "editeurevent_Sequence")
    @SequenceGenerator(name = "editeurevent_Sequence", sequenceName = "EDITEUREVENT_SEQ", allocationSize = 1)
    public Long id;

    @Column(name = "DATE_CREATION_EVENT")
    public Date dateCreationEvent;

    @Column(name = "EVENT")
    public String event;

    @Column(name = "NOM_EDITEUR")
    public String nomEditeur;

    @Column(name = "IDENTIFIANT_EDITEUR")
    public String identifiantEditeur;

    @Column(name = "ADRESSE_EDITEUR")
    public String adresseEditeur;

    @Lob
    @Column(name = "GROUPES_ETAB_RELIES")
    public List<String> groupesEtabRelies = new ArrayList<>();

    @Lob
    @Column(name = "LISTE_CONTACT_COMMERCIAL_EDITEURDTO", columnDefinition = "CLOB")
    public String listeContactCommercialEditeur;

    @Lob
    @Column(name = "LISTE_CONTACT_TECHNIQUE_EDITEURDTO")
    public String listeContactTechniqueEditeur;

    @Lob
    @Column(name = "ID_EDITEUR_FUSIONNES")
    private List<Long> idEditeurFusionnes;

    public EditeurEventEntity(EditeurCreeEvent editeurCreeEvent) throws JsonProcessingException {
        this.event = "editeurCree";
        this.dateCreationEvent = editeurCreeEvent.created;
        this.nomEditeur = editeurCreeEvent.getNomEditeur();
        this.identifiantEditeur = editeurCreeEvent.getIdentifiantEditeur();
        this.groupesEtabRelies = editeurCreeEvent.getGroupesEtabRelies();
        this.adresseEditeur = editeurCreeEvent.getAdresseEditeur();
        this.listeContactCommercialEditeur = mapper.writeValueAsString(editeurCreeEvent.getListeContactCommercialEditeur());
        this.listeContactTechniqueEditeur = mapper.writeValueAsString(editeurCreeEvent.getListeContactTechniqueEditeur());
    }

    public EditeurEventEntity(EditeurModifieEvent editeurModifieEvent) throws JsonProcessingException {
        this.event = "editeurModifie";
        this.dateCreationEvent = editeurModifieEvent.created;
        this.nomEditeur = editeurModifieEvent.getNomEditeur();
        this.identifiantEditeur = editeurModifieEvent.getIdentifiantEditeur();
        this.groupesEtabRelies = editeurModifieEvent.getGroupesEtabRelies();
        this.adresseEditeur = editeurModifieEvent.getAdresseEditeur();
        this.listeContactCommercialEditeur = mapper.writeValueAsString(editeurModifieEvent.getListeContactCommercialEditeur());
        this.listeContactTechniqueEditeur = mapper.writeValueAsString(editeurModifieEvent.getListeContactTechniqueEditeur());
    }

    public EditeurEventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        this.event = "editeurFusione";
        this.dateCreationEvent = editeurFusionneEvent.created;
        this.nomEditeur = editeurFusionneEvent.getNomEditeur();
        this.adresseEditeur = editeurFusionneEvent.getAdresseEditeur();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    public EditeurEventEntity(EditeurSupprimeEvent editeurSupprimeEvent) {
        this.event = "editeurSupprime";
        this.dateCreationEvent = editeurSupprimeEvent.created;
        //this.id = editeurSupprimeEvent.getId(); ==> Error attempting to apply AttributeConverter
    }
}
