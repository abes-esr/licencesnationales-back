package fr.abes.licencesnationales.core.entities.etablissement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.entities.EventEntity;
import fr.abes.licencesnationales.core.event.etablissement.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "EtablissementEvent")
@NoArgsConstructor
@Getter
@Setter
public class EtablissementEventEntity extends EventEntity implements Serializable {

    @Autowired
    @Transient
    private ObjectMapper mapper;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etabevent_Sequence")
    @SequenceGenerator(name = "etabevent_Sequence", sequenceName = "ETABEVENT_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "NOM_ETAB")
    private String nomEtab;

    @Column(name = "SIREN")
    private String siren;

    @Column(name = "ADRESSE")
    private String adresse;

    @Column(name = "TYPE_ETABLISSEMENT")
    private String typeEtablissement;

    @Column(name = "MOT_DE_PASSE")
    private String motDePasse;

    @Column(name = "ID_ABES")
    private String idAbes;

    @Column(name = "MAIL_CONTACT")
    private String mailContact;

    @Column(name = "NOM_CONTACT")
    private String nomContact;

    @Column(name = "PRENOM_CONTACT")
    private String prenomContact;

    @Column(name = "TELEPHONE_CONTACT")
    private String telephoneContact;

    @Column(name = "ADRESSE_CONTACT")
    private String adresseContact;

    @Column(name = "BOITE_POSTALE_CONTACT")
    private String boitePostaleContact;

    @Column(name = "CODE_POSTAL_CONTACT")
    private String codePostalContact;

    @Column(name = "CEDEX_CONTACT")
    private String cedexContact;

    @Column(name = "VILLE_CONTACT")
    private String villeContact;

    @Column(name = "ROLE_CONTACT")
    private String roleContact;

    @Column(name = "ANCIEN_NOM_ETAB")
    private String ancienNomEtab;

    //TODO: Définir la différence avec etablissementsFusionne
    @Lob
    @Column(name = "ETABLISSEMENTDTOFUSION")
    private String etablissementsFusionnes;

    //TODO: Définir la différence avec etablissementsFusionnes
    @Lob
    @Column(name = "ETABLISSEMENTS_FUSIONNE")
    private String etablissementsFusionne;

    @Lob
    @Column(name = "ETABLISEMENTS_DIVISE")
    private String etablisementsDivises;

    /**
     * CTOR à partir d'un événement de création d'établissement
     *
     * @param etablissementCreeEvent Evénement de création d'un établissement
     */
    public EtablissementEventEntity(EtablissementCreeEvent etablissementCreeEvent) {
        this.event = "cree";
        this.dateCreationEvent = etablissementCreeEvent.created;
        this.nomEtab = etablissementCreeEvent.getNom();
        this.siren = etablissementCreeEvent.getSiren();
        this.typeEtablissement = etablissementCreeEvent.getTypeEtablissement();
        this.motDePasse = etablissementCreeEvent.getMotDePasse();
        this.idAbes = etablissementCreeEvent.getIdAbes();
        this.mailContact = etablissementCreeEvent.getMailContact();
        this.nomContact = etablissementCreeEvent.getNomContact();
        this.prenomContact = etablissementCreeEvent.getPrenomContact();
        this.telephoneContact = etablissementCreeEvent.getTelephoneContact();
        this.adresseContact = etablissementCreeEvent.getAdresseContact();
        this.boitePostaleContact = etablissementCreeEvent.getBoitePostaleContact();
        this.codePostalContact = etablissementCreeEvent.getCodePostalContact();
        this.cedexContact = etablissementCreeEvent.getCedexContact();
        this.villeContact = etablissementCreeEvent.getVilleContact();
        this.roleContact = etablissementCreeEvent.getRoleContact();
    }

    /**
     * CTOR à partir d'un événement de modification d'établissement
     *
     * @param etablissementModifieEvent Evénement de modification d'un établissement
     */
    public EtablissementEventEntity(EtablissementModifieEvent etablissementModifieEvent) {
        this.event = "modifie";
        this.dateCreationEvent = etablissementModifieEvent.created;
        this.siren = etablissementModifieEvent.getSiren();
        this.nomContact = etablissementModifieEvent.getNomContact();
        this.prenomContact = etablissementModifieEvent.getPrenomContact();
        this.adresseContact = etablissementModifieEvent.getAdresseContact();
        this.mailContact = etablissementModifieEvent.getMailContact();
        this.telephoneContact = etablissementModifieEvent.getTelephoneContact();
        this.boitePostaleContact = etablissementModifieEvent.getBoitePostaleContact();
        this.codePostalContact = etablissementModifieEvent.getCodePostalContact();
        this.villeContact = etablissementModifieEvent.getVilleContact();
        this.cedexContact = etablissementModifieEvent.getCedexContact();
    }

    /**
     * CTOR à partir d'un événement de suppression d'établissement
     *
     * @param etablissementSupprimeEvent Evénement de suppression d'un établissement
     */
    public EtablissementEventEntity(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.dateCreationEvent = etablissementSupprimeEvent.created;
        this.nomEtab = etablissementSupprimeEvent.getSiren();
    }

    /**
     * CTOR à partir d'un événement de division d'établissement
     *
     * @param etablissementDiviseEvent Evénement de division d'un établissement
     * @throws JsonProcessingException Si les établissement divisés n'ont pas pu être transformé
     *                                 en chaîne de caractère pour l'insertion dans la base de données.
     */
    public EtablissementEventEntity(EtablissementDiviseEvent etablissementDiviseEvent) throws JsonProcessingException {
        this.event = "divise";
        this.dateCreationEvent = etablissementDiviseEvent.created;
        this.ancienNomEtab = etablissementDiviseEvent.getAncienSiren();
        this.etablisementsDivises = mapper.writeValueAsString(etablissementDiviseEvent.getEtablissements());
    }

    /**
     * CTOR à partir d'un événement de fusion d'établissement
     *
     * @param etablissementFusionneEvent Evénement de la fusion de deux établissements
     * @throws JsonProcessingException Si les établissement fusionnées n'ont pas pu être transformé
     *                                 en chaîne de caractère pour l'insertion dans la base de données.
     */
    public EtablissementEventEntity(EtablissementFusionneEvent etablissementFusionneEvent) throws JsonProcessingException {
        this.event = "fusionne";
        this.dateCreationEvent = etablissementFusionneEvent.created;
        ContactEntity contact = new ContactEntity(etablissementFusionneEvent.getNomContact(), etablissementFusionneEvent.getPrenomContact(), etablissementFusionneEvent.getAdresseContact(),
                etablissementFusionneEvent.getBoitePostaleContact(), etablissementFusionneEvent.getCodePostalContact(), etablissementFusionneEvent.getVilleContact(), etablissementFusionneEvent.getCedexContact(),
                etablissementFusionneEvent.getTelephoneContact(), etablissementFusionneEvent.getMailContact());
        this.etablissementsFusionnes = mapper.writeValueAsString(etablissementFusionneEvent);
        this.etablissementsFusionne = mapper.writeValueAsString(etablissementFusionneEvent.getSirenFusionne());
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

        return id != null && id.equals(((EtablissementEventEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "EditeurEventEntity {" + "id=" + id + ", événement=" + event + ", nom de l'établissement=" + nomEtab + " }";
    }
}
