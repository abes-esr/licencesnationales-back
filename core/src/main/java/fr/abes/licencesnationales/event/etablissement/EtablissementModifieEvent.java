package fr.abes.licencesnationales.event.etablissement;


import fr.abes.licencesnationales.event.Event;
import lombok.Getter;

@Getter
public class EtablissementModifieEvent extends Event {
    private String siren;
    private String nomContact;
    private String prenomContact;
    private String mailContact;
    private String telephoneContact;
    private String adresseContact;
    private String boitePostaleContact;
    private String codePostalContact;
    private String villeContact;
    private String cedexContact;

    public EtablissementModifieEvent(Object source, String siren, String nomContact, String prenomContact, String mailContact, String telephoneContact, String adresseContact, String boitePostaleContact, String codePostalContact, String villeContact, String cedexContact) {
        super(source);
        this.siren = siren;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.mailContact = mailContact;
        this.telephoneContact = telephoneContact;
        this.adresseContact = adresseContact;
        this.boitePostaleContact = boitePostaleContact;
        this.codePostalContact = codePostalContact;
        this.villeContact = villeContact;
        this.cedexContact = cedexContact;
    }
}
