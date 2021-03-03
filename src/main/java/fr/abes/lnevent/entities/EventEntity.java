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
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Event")
@NoArgsConstructor
public class EventEntity {

    public EventEntity(EtablissementCreeEvent etablissementCreeEvent) {
        EtablissementDTO etablissement = etablissementCreeEvent.getEtablissement();
        this.event = "cree";
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
    }

    public EventEntity(EtablissementModifieEvent etablissementModifieEvent) {
        EtablissementDTO etablissement = etablissementModifieEvent.getEtablissement();
        this.event = "modifie";
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
        this.roleContact = etablissementModifieEvent.getRoleContact();
    }

    public EventEntity(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.nomEtab = etablissementSupprimeEvent.getSiren();
    }

    public EventEntity(EtablissementDiviseEvent etablissementDiviseEvent) {
        this.event = "divise";
        this.ancienNomEtab = etablissementDiviseEvent.getAncienSiren();
        this.etablisementsDivise = etablissementDiviseEvent.getEtablissementDTOS();
    }

    public EventEntity(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.etablissementDTOFusion = etablissementFusionneEvent.getEtablissementDTO();
        this.etablissementsFusionne = etablissementFusionneEvent.getSirenFusionne();
    }

    public EventEntity(EditeurCreeEvent editeurCreeEvent) {
        this.event = "editeurcree";
        this.nomEditeur = editeurCreeEvent.getNom();
        this.adresseEditeur = editeurCreeEvent.getAdresse();
        this.mailPourBatchEditeur = editeurCreeEvent.getMailPourBatch();
        this.mailPourInformationEditeur = editeurCreeEvent.getMailPourInformation();
    }

    public EventEntity(EditeurModifieEvent editeurModifieEvent) {
        this.event = "editeurmodifie";
        this.nomEditeur = editeurModifieEvent.getNom();
        this.adresseEditeur = editeurModifieEvent.getAdresse();
        this.mailPourBatchEditeur = editeurModifieEvent.getMailPourBatch();
        this.mailPourInformationEditeur = editeurModifieEvent.getMailPourInformation();
    }

    public EventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        this.event = "editeurfusione";
        this.nomEditeur = editeurFusionneEvent.getEditeurDTO().getNom();
        this.adresseEditeur = editeurFusionneEvent.getEditeurDTO().getAdresse();
        this.mailPourBatchEditeur = editeurFusionneEvent.getEditeurDTO().getMailPourBatch();
        this.mailPourInformationEditeur = editeurFusionneEvent.getEditeurDTO().getMailPourInformation();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    public EventEntity(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoute";
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
    }

    public EventEntity(IpModifieeEvent ipModifieeEvent) {
        this.event = "ipModifie";
        this.ip = ipModifieeEvent.getIp();
        this.siren = ipModifieeEvent.getSiren();
    }

    public EventEntity(IpValideeEvent ipValideeEvent) {
        this.event = "ipValidee";
        this.ip = ipValideeEvent.getIp();
        this.siren = ipValideeEvent.getSiren();
    }

    public EventEntity(IpSupprimeeEvent ipSupprimeeEvent) {
        this.event = "ipSupprimee";
        this.ip = ipSupprimeeEvent.getIp();
        this.siren = ipSupprimeeEvent.getSiren();
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "event_Sequence")
    @SequenceGenerator(name = "event_Sequence", sequenceName = "EVENT_SEQ", allocationSize = 1)
    public Long id;

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
