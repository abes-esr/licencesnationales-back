package fr.abes.lnevent.entities;

import fr.abes.lnevent.event.editeur.EditeurCreeEvent;
import fr.abes.lnevent.event.editeur.EditeurFusionneEvent;
import fr.abes.lnevent.event.editeur.EditeurModifieEvent;
import fr.abes.lnevent.dto.etablissement.Etablissement;
import fr.abes.lnevent.event.etablissement.*;
import fr.abes.lnevent.event.ip.IpAjouteeEvent;
import fr.abes.lnevent.event.ip.IpModifieeEvent;
import fr.abes.lnevent.event.ip.IpSupprimeeEvent;
import fr.abes.lnevent.event.ip.IpValideeEvent;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Event")
@NoArgsConstructor
public class EventRow {

    public EventRow(EtablissementCreeEvent etablissementCreeEvent) {
        this.event = "cree";
        this.nomEtab = etablissementCreeEvent.getNom();
        this.adresse = etablissementCreeEvent.getAdresse();
        this.siren = etablissementCreeEvent.getSiren();
        this.typeEtablissement = etablissementCreeEvent.getTypeEtablissement();
        this.motDePasse = etablissementCreeEvent.getMotDePasse();
        this.idAbes = etablissementCreeEvent.getIdAbes();
        this.mailContact = etablissementCreeEvent.getMailContact();
        this.nomContact = etablissementCreeEvent.getNomContact();
        this.prenomContact = etablissementCreeEvent.getPrenomContact();
        this.telephoneContact = etablissementCreeEvent.getTelephoneContact();
        this.adresseContact = etablissementCreeEvent.getAdresseContact();
    }

    public EventRow(EtablissementModifieEvent etablissementModifieEvent) {
        this.event = "modifie";
        this.nomEtab = etablissementModifieEvent.getNom();
        this.adresse = etablissementModifieEvent.getAdresse();
        this.siren = etablissementModifieEvent.getSiren();
        this.typeEtablissement = etablissementModifieEvent.getTypeEtablissement();
        this.motDePasse = etablissementModifieEvent.getMotDePasse();
        this.idAbes = etablissementModifieEvent.getIdAbes();
        this.mailContact = etablissementModifieEvent.getMailContact();
        this.nomContact = etablissementModifieEvent.getNomContact();
        this.prenomContact = etablissementModifieEvent.getPrenomContact();
        this.telephoneContact = etablissementModifieEvent.getTelephoneContact();
        this.adresseContact = etablissementModifieEvent.getAdresseContact();
    }

    public EventRow(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.nomEtab = etablissementSupprimeEvent.getSiren();
    }

    public EventRow(EtablissementDiviseEvent etablissementDiviseEvent) {
        this.event = "divise";
        this.ancienNomEtab = etablissementDiviseEvent.getAncienSiren();
        this.etablisementsDivise = etablissementDiviseEvent.getEtablissements();
    }

    public EventRow(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.etablissementFusion = etablissementFusionneEvent.getEtablissement();
        this.etablissementsFusionne = etablissementFusionneEvent.getSirenFusionne();
    }

    public EventRow(EditeurCreeEvent editeurCreeEvent) {
        this.event = "editeurcree";
        this.nomEditeur = editeurCreeEvent.getNom();
        this.adresseEditeur = editeurCreeEvent.getAdresse();
        this.mailPourBatchEditeur = editeurCreeEvent.getMailPourBatch();
        this.mailPourInformationEditeur = editeurCreeEvent.getMailPourInformation();
    }

    public EventRow(EditeurModifieEvent editeurModifieEvent) {
        this.event = "editeurmodifie";
        this.nomEditeur = editeurModifieEvent.getNom();
        this.adresseEditeur = editeurModifieEvent.getAdresse();
        this.mailPourBatchEditeur = editeurModifieEvent.getMailPourBatch();
        this.mailPourInformationEditeur = editeurModifieEvent.getMailPourInformation();
    }

    public EventRow(EditeurFusionneEvent editeurFusionneEvent) {
        this.event = "editeurfusione";
        this.nomEditeur = editeurFusionneEvent.getEditeur().getNom();
        this.adresseEditeur = editeurFusionneEvent.getEditeur().getAdresse();
        this.mailPourBatchEditeur = editeurFusionneEvent.getEditeur().getMailPourBatch();
        this.mailPourInformationEditeur = editeurFusionneEvent.getEditeur().getMailPourInformation();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    public EventRow(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoute";
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
    }

    public EventRow(IpModifieeEvent ipModifieeEvent) {
        this.event = "ipModifie";
        this.ip = ipModifieeEvent.getIp();
        this.siren = ipModifieeEvent.getSiren();
    }

    public EventRow(IpValideeEvent ipValideeEvent) {
        this.event = "ipValidee";
        this.ip = ipValideeEvent.getIp();
        this.siren = ipValideeEvent.getSiren();
    }

    public EventRow(IpSupprimeeEvent ipSupprimeeEvent) {
        this.event = "ipSupprimee";
        this.ip = ipSupprimeeEvent.getIp();
        this.siren = ipSupprimeeEvent.getSiren();
    }

    @Id
    public String id;

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

    public String ancienNomEtab;

    public Etablissement etablissementFusion;

    public List<Etablissement> etablisementsDivise;

    public List<String> etablissementsFusionne;

    public String ip;

    public String siren;

    private String nomEditeur;

    private String adresseEditeur;

    private List<String> mailPourBatchEditeur;

    private List<String> mailPourInformationEditeur;

    private List<String> idEditeurFusionnes;



}
