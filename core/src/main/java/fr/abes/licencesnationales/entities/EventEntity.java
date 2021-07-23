package fr.abes.licencesnationales.entities;

import fr.abes.licencesnationales.converter.*;
import fr.abes.licencesnationales.dto.editeur.ContactCommercialEditeurDTO;
import fr.abes.licencesnationales.dto.editeur.ContactTechniqueEditeurDTO;
import fr.abes.licencesnationales.dto.editeur.EditeurDTO;
import fr.abes.licencesnationales.dto.etablissement.EtablissementDTO;
import fr.abes.licencesnationales.event.editeur.EditeurCreeEvent;
import fr.abes.licencesnationales.event.editeur.EditeurModifieEvent;
import fr.abes.licencesnationales.event.editeur.EditeurSupprimeEvent;
import fr.abes.licencesnationales.event.etablissement.*;
import fr.abes.licencesnationales.event.ip.IpAjouteeEvent;
import fr.abes.licencesnationales.event.ip.IpModifieeEvent;
import fr.abes.licencesnationales.event.ip.IpSupprimeeEvent;
import fr.abes.licencesnationales.event.ip.IpValideeEvent;
import fr.abes.licencesnationales.event.password.UpdatePasswordEvent;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Event")
@NoArgsConstructor
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

    public String identifiantEditeur;

    public String adresseEditeur;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public List<String> groupesEtabRelies = new ArrayList<>();

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public Set<ContactCommercialEditeurDTO> listeContactCommercialEditeurDTO;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    public Set<ContactTechniqueEditeurDTO> listeContactTechniqueEditeurDTO;

    /*@Lob
    @Convert(fr.abes.licencesnationales.converter = JpaConverterJson.class)
    private List<String> mailPourBatchEditeur;

    @Lob
    @Convert(fr.abes.licencesnationales.converter = JpaConverterJson.class)
    private List<String> mailPourInformationEditeur;*/

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<Long> idEditeurFusionnes;



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
        EditeurDTO editeurDTO = editeurCreeEvent.getEditeurDTO();
        this.event = "editeurcree";
        this.dateCreationEvent = editeurCreeEvent.created;
        this.nomEditeur = editeurDTO.getNomEditeur();
        this.identifiantEditeur = editeurDTO.getIdentifiantEditeur();
        this.groupesEtabRelies = editeurDTO.getGroupesEtabRelies();
        this.adresseEditeur = editeurDTO.getAdresseEditeur();
        this.listeContactCommercialEditeurDTO = editeurDTO.getListeContactCommercialEditeurDTO();
        this.listeContactTechniqueEditeurDTO = editeurDTO.getListeContactTechniqueEditeurDTO();
    }

    public EventEntity(EditeurModifieEvent editeurModifieEvent) {
        this.event = "editeurmodifie";
        this.dateCreationEvent = editeurModifieEvent.created;
        this.nomEditeur = editeurModifieEvent.getNomEditeur();
        this.identifiantEditeur = editeurModifieEvent.getIdentifiantEditeur();
        this.groupesEtabRelies = editeurModifieEvent.getGroupesEtabRelies();
        this.adresseEditeur = editeurModifieEvent.getAdresseEditeur();
        this.listeContactCommercialEditeurDTO = editeurModifieEvent.getContactCommercialEditeurDTOS();
        this.listeContactTechniqueEditeurDTO = editeurModifieEvent.getContactTechniqueEditeurDTOS();
    }

    /*public EventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        this.fr.abes.licencesnationales.event = "editeurfusione";
        this.dateCreationEvent = editeurFusionneEvent.created;
        this.nomEditeur = editeurFusionneEvent.getEditeurDTO().getNom();
        this.adresseEditeur = editeurFusionneEvent.getEditeurDTO().getAdresse();
        this.mailPourBatchEditeur = editeurFusionneEvent.getEditeurDTO().getMailPourBatch();
        this.mailPourInformationEditeur = editeurFusionneEvent.getEditeurDTO().getMailPourInformation();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }*/

    public EventEntity(EditeurSupprimeEvent editeurSupprimeEvent) {
        this.event = "editeurSupprime";
        this.dateCreationEvent = editeurSupprimeEvent.created;
        this.id = Long.parseLong(editeurSupprimeEvent.getId());
        //this.siren = editeurSupprimeEvent.getSiren();
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
