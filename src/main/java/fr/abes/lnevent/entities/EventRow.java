package fr.abes.lnevent.entities;

import fr.abes.lnevent.event.*;
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

    public EventRow(EtablissementSupprimeEvent etablissementSupprimeEvent) {
        this.event = "supprime";
        this.nomEtab = etablissementSupprimeEvent.getSiren();
    }

    public EventRow(EtablissementDiviseEvent etablissementDiviseEvent) {
        this.event = "divise";
        this.ancienNomEtab = etablissementDiviseEvent.getAncienSiren();
        this.etablisementsDivise = etablissementDiviseEvent.getSirens();
    }

    public EventRow(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.nomEtab = etablissementFusionneEvent.getSiren();
        this.etablissementsFusionne = etablissementFusionneEvent.getSirenFusionne();
    }

    public EventRow(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoute";
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
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

    public List<String> etablisementsDivise;

    public List<String> etablissementsFusionne;

    public String ip;

    public String siren;


}
