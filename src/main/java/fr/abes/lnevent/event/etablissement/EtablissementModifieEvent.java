package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class EtablissementModifieEvent extends Event {
    public EtablissementModifieEvent(Object source, Long idEtablissement, String nom, String adresse, String siren, String typeEtablissement, String motDePasse, String idAbes, String idContact, String mailContact, String nomContact, String prenomContact, String telephoneContact, String adresseContact) {
        super(source);
        this.idEtablissement = idEtablissement;
        this.nom = nom;
        this.adresse = adresse;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.motDePasse = motDePasse;
        this.idAbes = idAbes;
        this.idContact = idContact;
        this.mailContact = mailContact;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.telephoneContact = telephoneContact;
        this.adresseContact = adresseContact;
    }

    private Long idEtablissement;

    private String nom;

    private String adresse;

    private String siren;

    private String typeEtablissement;

    private String motDePasse;

    private String idAbes;

    private String idContact;

    private String mailContact;

    private String nomContact;

    private String prenomContact;

    private String telephoneContact;

    private String adresseContact;
}
