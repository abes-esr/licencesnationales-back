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
@NoArgsConstructor
@Getter
public class EditeurEventEntity implements Serializable {

    @Autowired
    @Transient
    private ObjectMapper mapper;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editeurevent_Sequence")
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

    /**
     * CTOR à partir d'un événement de création d'éditeur
     *
     * @param editeurCreeEvent Evénement de création d'un éditeur
     * @throws JsonProcessingException Si le contact commercial ou le contact technique n'a pas pu être transformé
     *                                 en chaîne de caractère pour l'insertion dans la base de données.
     */
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

    /**
     * CTOR à partir d'un événement de modification d'éditeur
     *
     * @param editeurModifieEvent Evénement de modification d'un éditeur
     * @throws JsonProcessingException Si le contact commercial ou le contact technique n'a pas pu être transformé
     *                                 en chaîne de caractère pour l'insertion dans la base de données.
     */
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

    /**
     * CTOR à partir d'un événement de fusion d'éditeur
     *
     * @param editeurFusionneEvent Evénement de fusion d'un éditeur
     */
    public EditeurEventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        this.event = "editeurFusione";
        this.dateCreationEvent = editeurFusionneEvent.created;
        this.nomEditeur = editeurFusionneEvent.getNomEditeur();
        this.adresseEditeur = editeurFusionneEvent.getAdresseEditeur();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    /**
     * CTOR à partir d'un événement de suppresion d'éditeur
     *
     * @param editeurSupprimeEvent Evénement de supression d'un éditeur
     */
    public EditeurEventEntity(EditeurSupprimeEvent editeurSupprimeEvent) {
        this.event = "editeurSupprime";
        this.dateCreationEvent = editeurSupprimeEvent.created;
        //this.id = editeurSupprimeEvent.getId(); ==> Error attempting to apply AttributeConverter
        // TODO : pourquoi cette ligne est commentée ?
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return id != null && id.equals(((EditeurEventEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "EditeurEventEntity {" + "id=" + id + ", événement=" + event + ", nom de l'éditeur=" + nomEditeur + " }";
    }
}
