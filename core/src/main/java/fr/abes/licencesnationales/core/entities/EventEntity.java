package fr.abes.licencesnationales.core.entities;

import fr.abes.licencesnationales.core.dto.editeur.*;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementCreeDto;
import fr.abes.licencesnationales.core.dto.etablissement.EtablissementDto;
import fr.abes.licencesnationales.core.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurFusionneEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.core.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.core.event.etablissement.*;
import fr.abes.licencesnationales.core.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.core.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.core.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.core.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.core.event.password.UpdatePasswordEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/** TODO : vérifier pourquoi les attributs sont publics
 * TODO : Voir comment récupérer l'entité concernée par l'event au lieu de l'intégralité des champs
 */
@Entity
@Table(name = "Event")
@NoArgsConstructor
@Getter
public class EventEntity {
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
    public EtablissementDto etablissementFusionneDto;

    @Lob
    @Convert(converter = ListEtablissementDTOConverter.class)
    public List<EtablissementDto> etablisementsDivise;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public List<String> etablissementsFusionne;

    public String ip;

    public String siren;

    public String nomEditeur;

    public String identifiantEditeur;

    public String adresseEditeur;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public List<String> groupesEtabRelies = new ArrayList<>();

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public Set<ContactCommercialEditeurDto> listeContactCommercialEditeurDto;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeurDto;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<Long> idEditeurFusionnes;



    public EventEntity(EtablissementCreeEvent etablissementCreeEvent) {
        EtablissementCreeDto etablissement = etablissementCreeEvent.getEtablissement();
        this.event = "cree";
        this.dateCreationEvent = etablissementCreeEvent.created;
        this.nomEtab = etablissement.getEtablissementDTO().getNom();
        this.siren = etablissement.getEtablissementDTO().getSiren();
        this.typeEtablissement = etablissement.getEtablissementDTO().getTypeEtablissement();
        this.motDePasse = etablissement.getEtablissementDTO().getMotDePasse();
        this.idAbes = etablissement.getEtablissementDTO().getIdAbes();
        this.mailContact = etablissement.getEtablissementDTO().getMailContact();
        this.nomContact = etablissement.getEtablissementDTO().getNomContact();
        this.prenomContact = etablissement.getEtablissementDTO().getPrenomContact();
        this.telephoneContact = etablissement.getEtablissementDTO().getTelephoneContact();
        this.adresseContact = etablissement.getEtablissementDTO().getAdresseContact();
        this.boitePostaleContact = etablissement.getEtablissementDTO().getBoitePostaleContact();
        this.codePostalContact = etablissement.getEtablissementDTO().getCodePostalContact();
        this.cedexContact = etablissement.getEtablissementDTO().getCedexContact();
        this.villeContact = etablissement.getEtablissementDTO().getVilleContact();
        this.roleContact = etablissement.getEtablissementDTO().getRoleContact();
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
        this.etablisementsDivise = etablissementDiviseEvent.getEtablissementDtos();
    }

    public EventEntity(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.dateCreationEvent = etablissementFusionneEvent.created;
        this.etablissementFusionneDto = etablissementFusionneEvent.getEtablissementDto();
        this.etablissementsFusionne = etablissementFusionneEvent.getSirenFusionne();
    }

    public EventEntity(EditeurCreeEvent editeurCreeEvent) {
        EditeurCreeDto editeur = editeurCreeEvent.getEditeur();
        this.event = "editeurcree";
        this.dateCreationEvent = editeurCreeEvent.created;
        this.nomEditeur = editeur.getNomEditeur();
        this.identifiantEditeur = editeur.getIdentifiantEditeur();
        this.groupesEtabRelies = editeur.getGroupesEtabRelies();
        this.adresseEditeur = editeur.getAdresseEditeur();
        this.listeContactCommercialEditeurDto = editeur.getListeContactCommercialEditeurDto();
        this.listeContactTechniqueEditeurDto = editeur.getListeContactTechniqueEditeurDto();
    }

    public EventEntity(EditeurModifieEvent editeurModifieEvent) {
        EditeurModifieDto editeur = editeurModifieEvent.getEditeur();
        this.event = "editeurmodifie";
        this.dateCreationEvent = editeurModifieEvent.created;
        this.nomEditeur = editeur.getNomEditeur();
        this.identifiantEditeur = editeur.getIdentifiantEditeur();
        this.groupesEtabRelies = editeur.getGroupesEtabRelies();
        this.adresseEditeur = editeur.getAdresseEditeur();
        this.listeContactCommercialEditeurDto = editeur.getListeContactCommercialEditeurDto();
        this.listeContactTechniqueEditeurDto = editeur.getListeContactTechniqueEditeurDto();
    }

    public EventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        EditeurFusionneDto editeur = editeurFusionneEvent.getEditeur();
        this.event = "editeurfusione";
        this.dateCreationEvent = editeurFusionneEvent.created;
        this.nomEditeur = editeur.getNomEditeur();
        this.adresseEditeur = editeur.getAdresseEditeur();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    public EventEntity(EditeurSupprimeEvent editeurSupprimeEvent) {
        this.event = "editeurSupprime";
        this.dateCreationEvent = editeurSupprimeEvent.created;
        this.id = Long.parseLong(editeurSupprimeEvent.getId());
    }

    public EventEntity(IpAjouteeEvent ipAjouteeEvent) {
        this.event = "ipAjoutee";
        this.dateCreationEvent = ipAjouteeEvent.created;
        this.ip = ipAjouteeEvent.getIp();
        this.siren = ipAjouteeEvent.getSiren();
        //this.typeAccesIp = ipAjouteeEvent.getTypeAcces();
        //this.typeIp = ipAjouteeEvent.getTypeIp();
        //this.dateCreationIp = ipAjouteeEvent.getDate();
    }

    public EventEntity(IpModifieeEvent ipModifieeEvent) {
        this.event = "ipModifiee";
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
        this.id = Long.parseLong(ipSupprimeeEvent.getId());
        this.siren = ipSupprimeeEvent.getSiren();
    }

    public EventEntity(UpdatePasswordEvent updatePasswordEvent) {
        this.event = "motDePasseMisAJour";
        this.dateCreationEvent = updatePasswordEvent.created;
        this.siren = updatePasswordEvent.getSiren();
        this.motDePasse = updatePasswordEvent.getNewpasswordHash();
    }



}
