package fr.abes.licencesnationales.core.entities.etablissement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@NoArgsConstructor @Getter @Setter
public class EtablissementEventEntity implements Serializable {
    @Autowired
    @Transient
    private ObjectMapper mapper;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "etabevent_Sequence")
    @SequenceGenerator(name = "etabevent_Sequence", sequenceName = "ETABEVENT_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "DATE_CREATION_EVENT")
    private Date dateCreationEvent;

    @Column(name = "EVENT")
    private String event;

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

    @Lob
    @Column(name = "ETABLISSEMENTDTOFUSION")
    private String etablissementsFusionnes;

    @Lob
    @Column(name = "ETABLISEMENTS_DIVISE")
    private String etablisementsDivises;

    @Lob
    @Column(name = "ETABLISSEMENTS_FUSIONNE")
    private String etablissementsFusionne;

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

    public EtablissementEventEntity(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.dateCreationEvent = etablissementSupprimeEvent.created;
        this.nomEtab = etablissementSupprimeEvent.getSiren();
    }

    public EtablissementEventEntity(EtablissementDiviseEvent etablissementDiviseEvent) throws JsonProcessingException {
        this.event = "divise";
        this.dateCreationEvent = etablissementDiviseEvent.created;
        this.ancienNomEtab = etablissementDiviseEvent.getAncienSiren();
        this.etablisementsDivises = mapper.writeValueAsString(etablissementDiviseEvent.getEtablissements());
    }

    public EtablissementEventEntity(EtablissementFusionneEvent etablissementFusionneEvent) throws JsonProcessingException {
        this.event = "fusionne";
        this.dateCreationEvent = etablissementFusionneEvent.created;
        ContactEntity contact = new ContactEntity(etablissementFusionneEvent.getNomContact(), etablissementFusionneEvent.getPrenomContact(), etablissementFusionneEvent.getAdresseContact(),
                etablissementFusionneEvent.getBoitePostaleContact(), etablissementFusionneEvent.getCodePostalContact(), etablissementFusionneEvent.getVilleContact(),etablissementFusionneEvent.getCedexContact(),
                etablissementFusionneEvent.getTelephoneContact(), etablissementFusionneEvent.getMailContact());
        this.etablissementsFusionnes = mapper.writeValueAsString(etablissementFusionneEvent);
        this.etablissementsFusionne = mapper.writeValueAsString(etablissementFusionneEvent.getSirenFusionne());
    }
}
