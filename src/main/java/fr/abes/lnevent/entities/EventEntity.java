package fr.abes.lnevent.entities;

import fr.abes.lnevent.converter.EtablissementDTOConverter;
import fr.abes.lnevent.converter.JpaConverterJson;
import fr.abes.lnevent.converter.ListEtablissementDTOConverter;
import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.editeur.EditeurCreeEvent;
import fr.abes.lnevent.event.editeur.EditeurFusionneEvent;
import fr.abes.lnevent.event.editeur.EditeurModifieEvent;
import fr.abes.lnevent.event.etablissement.*;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.event.ip.IpValideeEvent;
import fr.abes.lnevent.event.password.UpdatePasswordEvent;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Event")
@NoArgsConstructor
public class EventEntity {

    public EventEntity(EtablissementCreeEvent etablissementCreeEvent) {
        EtablissementDTO etablissement = etablissementCreeEvent.getEtablissement();
        this.event = "cree";
        this.dateCreationEvent = etablissementCreeEvent.created;
        this.nomEtab = etablissement.getNom();
        this.siren = etablissement.getSiren();
        this.typeEtablissement = etablissement.getTypeEtablissement();
        this.motDePasse = etablissement.getMotDePasse();
        this.idAbes = etablissement.getIdAbes();
        this.mailContact = etablissement.getMailContact();
        this.nomContact = etablissement.getNomContact();
        this.prenomContact = etablissement.getPrenomContact();
        this.telephoneContact = etablissement.getTelephoneContact();
        this.adresseContact = etablissement.getAdresseContact();
        this.boitePostaleContact = etablissement.getBoitePostaleContact();
        this.codePostalContact = etablissement.getCodePostalContact();
        this.cedexContact = etablissement.getCedexContact();
        this.villeContact = etablissement.getVilleContact();
        this.roleContact = etablissement.getRoleContact();
    }

    public EventEntity(EtablissementModifieEvent etablissementModifieEvent) {
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

    public EventEntity(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.dateCreationEvent = etablissementSupprimeEvent.created;
        this.nomEtab = etablissementSupprimeEvent.getSiren();
    }

    public EventEntity(EtablissementDiviseEvent etablissementDiviseEvent) {
        this.event = "divise";
        this.dateCreationEvent = etablissementDiviseEvent.created;
        this.ancienNomEtab = etablissementDiviseEvent.getAncienSiren();
        this.etablisementsDivise = etablissementDiviseEvent.getEtablissementDTOS();
    }

    public EventEntity(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.dateCreationEvent = etablissementFusionneEvent.created;
        this.etablissementDTOFusion = etablissementFusionneEvent.getEtablissementDTO();
        this.etablissementsFusionne = etablissementFusionneEvent.getSirenFusionne();
    }

    public EventEntity(EditeurCreeEvent editeurCreeEvent) {
        this.event = "editeurcree";
        this.dateCreationEvent = editeurCreeEvent.created;
        this.nomEditeur = editeurCreeEvent.getNom();
        this.adresseEditeur = editeurCreeEvent.getAdresse();
        this.mailPourBatchEditeur = editeurCreeEvent.getMailPourBatch();
        this.mailPourInformationEditeur = editeurCreeEvent.getMailPourInformation();
    }

    public EventEntity(EditeurModifieEvent editeurModifieEvent) {
        this.event = "editeurmodifie";
        this.dateCreationEvent = editeurModifieEvent.created;
        this.nomEditeur = editeurModifieEvent.getNom();
        this.adresseEditeur = editeurModifieEvent.getAdresse();
        this.mailPourBatchEditeur = editeurModifieEvent.getMailPourBatch();
        this.mailPourInformationEditeur = editeurModifieEvent.getMailPourInformation();
    }

    public EventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        this.event = "editeurfusione";
        this.dateCreationEvent = editeurFusionneEvent.created;
        this.nomEditeur = editeurFusionneEvent.getEditeurDTO().getNom();
        this.adresseEditeur = editeurFusionneEvent.getEditeurDTO().getAdresse();
        this.mailPourBatchEditeur = editeurFusionneEvent.getEditeurDTO().getMailPourBatch();
        this.mailPourInformationEditeur = editeurFusionneEvent.getEditeurDTO().getMailPourInformation();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    public EventEntity(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoute";
        this.dateCreationEvent = ipAjouteeEvent.created;
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
    }

    public EventEntity(IpModifieeEvent ipModifieeEvent) {
        this.event = "ipModifie";
        this.dateCreationEvent = ipModifieeEvent.created;
        this.ip = ipModifieeEvent.getIp();
        this.siren = ipModifieeEvent.getSiren();
    }

    public EventEntity(IpValideeEvent ipValideeEvent) {
        this.event = "ipValidee";
        this.dateCreationEvent = ipValideeEvent.created;
        this.ip = ipValideeEvent.getIp();
        this.siren = ipValideeEvent.getSiren();
    }

    public EventEntity(IpSupprimeeEvent ipSupprimeeEvent) {
        this.event = "ipSupprimee";
        this.dateCreationEvent = ipSupprimeeEvent.created;
        this.ip = ipSupprimeeEvent.getIp();
        this.siren = ipSupprimeeEvent.getSiren();
    }

    public EventEntity(UpdatePasswordEvent updatePasswordEvent) {
        this.event = "motDePasseMisAJour";
        this.dateCreationEvent = updatePasswordEvent.created;
        this.siren = updatePasswordEvent.getSiren();
        this.motDePasse = updatePasswordEvent.getNewpasswordHash();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "event_Sequence")
    @SequenceGenerator(name = "event_Sequence", sequenceName = "EVENT_SEQ", allocationSize = 1)
    public Long id;

    public Date dateCreationEvent;

    public String event;

    public String nomEtab;

    public String adresse;

    public String typeEtablissement;

    public String motDePasse;

    public String idAbes;

    public String mailContact;

    public String nomContact;

    public String prenomContact;

    public String telephoneContact;

    public String adresseContact;

    public String boitePostaleContact;

    public String codePostalContact;

    public String cedexContact;

    public String villeContact;

    public String roleContact;

    public String ancienNomEtab;

    @Lob
    @Convert(converter = EtablissementDTOConverter.class)
    public EtablissementDTO etablissementDTOFusion;

    @Lob
    @Convert(converter = ListEtablissementDTOConverter.class)
    public List<EtablissementDTO> etablisementsDivise;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public List<String> etablissementsFusionne;

    public String ip;

    public String siren;

    public String nomEditeur;

    public String adresseEditeur;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<String> mailPourBatchEditeur;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<String> mailPourInformationEditeur;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<Long> idEditeurFusionnes;

}
