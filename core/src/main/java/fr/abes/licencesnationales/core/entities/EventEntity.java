package fr.abes.licencesnationales.core.entities;

import fr.abes.licencesnationales.core.converter.EtablissementDTOConverter;
import fr.abes.licencesnationales.core.converter.JpaConverterJson;
import fr.abes.licencesnationales.core.converter.ListEtablissementDTOConverter;
import fr.abes.licencesnationales.core.dto.contact.*;
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

    @Column(name = "DATE_CREATION_EVENT")
    public Date dateCreationEvent;

    @Column(name = "EVENT")
    public String event;

    @Column(name = "NOM_ETAB")
    public String nomEtab;

    @Column(name = "ADRESSE")
    public String adresse;

    @Column(name = "TYPE_ETABLISSEMENT")
    public String typeEtablissement;

    @Column(name = "MOT_DE_PASSE")
    public String motDePasse;

    @Column(name = "ID_ABES")
    public String idAbes;

    @Column(name = "MAIL_CONTACT")
    public String mailContact;

    @Column(name = "NOM_CONTACT")
    public String nomContact;

    @Column(name = "PRENOM_CONTACT")
    public String prenomContact;

    @Column(name = "TELEPHONE_CONTACT")
    public String telephoneContact;

    @Column(name = "ADRESSE_CONTACT")
    public String adresseContact;

    @Column(name = "BOITE_POSTALE_CONTACT")
    public String boitePostaleContact;

    @Column(name = "CODE_POSTAL_CONTACT")
    public String codePostalContact;

    @Column(name = "CEDEX_CONTACT")
    public String cedexContact;

    @Column(name = "VILLE_CONTACT")
    public String villeContact;

    @Column(name = "ROLE_CONTACT")
    public String roleContact;

    @Column(name = "ANCIEN_NOM_ETAB")
    public String ancienNomEtab;

    @Lob
    @Convert(converter = EtablissementDTOConverter.class)
    @Column(name = "ETABLISSEMENTDTOFUSION")
    public EtablissementDto etablissementsFusionnes;

    @Lob
    @Convert(converter = ListEtablissementDTOConverter.class)
    @Column(name = "ETABLISEMENTS_DIVISE")
    public List<EtablissementDto> etablisementsDivises;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    @Column(name = "ETABLISSEMENTS_FUSIONNE")
    public List<String> etablissementsFusionne;

    @Column(name = "IP")
    public String ip;

    @Column(name = "SIREN")
    public String siren;

    @Column(name = "NOM_EDITEUR")
    public String nomEditeur;

    @Column(name = "IDENTIFIANT_EDITEUR")
    public String identifiantEditeur;

    @Column(name = "ADRESSE_EDITEUR")
    public String adresseEditeur;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    @Column(name = "GROUPES_ETAB_RELIES")
    public List<String> groupesEtabRelies = new ArrayList<>();

    @Lob
    @Convert(converter = JpaConverterJson.class)
    @Column(name = "LISTE_CONTACT_COMMERCIAL_EDITEURDTO")
    public Set<ContactCommercialEditeurDto> listeContactCommercialEditeurDto;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    @Column(name = "LISTE_CONTACT_TECHNIQUE_EDITEURDTO")
    public Set<ContactTechniqueEditeurDto> listeContactTechniqueEditeurDto;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    @Column(name = "ID_EDITEUR_FUSIONNES")
    private List<Long> idEditeurFusionnes;



    public EventEntity(EtablissementCreeEvent etablissementCreeEvent) {
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
        this.etablisementsDivises = etablissementDiviseEvent.getEtablissementDtos();
    }

    public EventEntity(EtablissementFusionneEvent etablissementFusionneEvent) {
        this.event = "fusionne";
        this.dateCreationEvent = etablissementFusionneEvent.created;
        this.etablissementsFusionnes = new EtablissementDto(etablissementFusionneEvent.getNom(), etablissementFusionneEvent.getSiren(), etablissementFusionneEvent.getTypeEtablissement(),
                etablissementFusionneEvent.getIdAbes(), etablissementFusionneEvent.getNomContact(), etablissementFusionneEvent.getPrenomContact(), etablissementFusionneEvent.getAdresseContact(),
                etablissementFusionneEvent.getBoitePostaleContact(), etablissementFusionneEvent.getCodePostalContact(), etablissementFusionneEvent.getVilleContact(),etablissementFusionneEvent.getCedexContact(),
                etablissementFusionneEvent.getTelephoneContact(), etablissementFusionneEvent.getMailContact());
        this.etablissementsFusionne = etablissementFusionneEvent.getSirenFusionne();
    }

    public EventEntity(EditeurCreeEvent editeurCreeEvent) {
        this.event = "editeurCree";
        this.dateCreationEvent = editeurCreeEvent.created;
        this.nomEditeur = editeurCreeEvent.getNomEditeur();
        this.identifiantEditeur = editeurCreeEvent.getIdentifiantEditeur();
        this.groupesEtabRelies = editeurCreeEvent.getGroupesEtabRelies();
        this.adresseEditeur = editeurCreeEvent.getAdresseEditeur();
        this.listeContactCommercialEditeurDto = editeurCreeEvent.getListeContactCommercialEditeur();
        this.listeContactTechniqueEditeurDto = editeurCreeEvent.getListeContactTechniqueEditeur();
    }

    public EventEntity(EditeurModifieEvent editeurModifieEvent) {
        this.event = "editeurModifie";
        this.dateCreationEvent = editeurModifieEvent.created;
        this.nomEditeur = editeurModifieEvent.getNomEditeur();
        this.identifiantEditeur = editeurModifieEvent.getIdentifiantEditeur();
        this.groupesEtabRelies = editeurModifieEvent.getGroupesEtabRelies();
        this.adresseEditeur = editeurModifieEvent.getAdresseEditeur();
        this.listeContactCommercialEditeurDto = editeurModifieEvent.getListeContactCommercialEditeur();
        this.listeContactTechniqueEditeurDto = editeurModifieEvent.getListeContactTechniqueEditeur();
    }

    public EventEntity(EditeurFusionneEvent editeurFusionneEvent) {
        this.event = "editeurFusione";
        this.dateCreationEvent = editeurFusionneEvent.created;
        this.nomEditeur = editeurFusionneEvent.getNomEditeur();
        this.adresseEditeur = editeurFusionneEvent.getAdresseEditeur();
        this.idEditeurFusionnes = editeurFusionneEvent.getIdEditeurFusionnes();
    }

    public EventEntity(EditeurSupprimeEvent editeurSupprimeEvent) {
        this.event = "editeurSupprime";
        this.dateCreationEvent = editeurSupprimeEvent.created;
        //this.id = editeurSupprimeEvent.getId(); ==> Error attempting to apply AttributeConverter
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
        this.id = ipSupprimeeEvent.getId();
        this.siren = ipSupprimeeEvent.getSiren();
    }

    public EventEntity(UpdatePasswordEvent updatePasswordEvent) {
        this.event = "motDePasseMisAJour";
        this.dateCreationEvent = updatePasswordEvent.created;
        this.siren = updatePasswordEvent.getSiren();
        this.motDePasse = updatePasswordEvent.getNewpasswordHash();
    }



}
