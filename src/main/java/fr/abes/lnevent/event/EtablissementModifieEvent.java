package fr.abes.lnevent.event;

public class EtablissementModifieEvent extends Event {
    public EtablissementModifieEvent(Object source, String nom, String adresse, String siren, String typeEtablissement, String motDePasse, String idAbes, String mailContact, String nomContact, String prenomContact, String telephoneContact, String adresseContact) {
        super(source);
        this.nom = nom;
        this.adresse = adresse;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.motDePasse = motDePasse;
        this.idAbes = idAbes;
        this.mailContact = mailContact;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.telephoneContact = telephoneContact;
        this.adresseContact = adresseContact;
    }

    private String nom;

    private String adresse;

    private String siren;

    private String typeEtablissement;

    private String motDePasse;

    private String idAbes;

    private String mailContact;

    private String nomContact;

    private String prenomContact;

    private String telephoneContact;

    private String adresseContact;
}
