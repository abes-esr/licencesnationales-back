package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.Event;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class EtablissementModifieEvent extends Event {



    private String siren;

    private String nomContact;
    private String mailContact;
    private String telephoneContact;
    private String adresseContact;
    private String boitePostaleContact;
    private String codePostalContact;
    private String villeContact;
    private String cedexContact;

    public EtablissementModifieEvent(Object source, String siren, String nomContact, String mailContact, String telephoneContact, String adresseContact, String boitePostaleContact, String codePostalContact, String villeContact, String cedexContact) {
        super(source);
        this.siren = siren;
        this.nomContact = nomContact;
        this.mailContact = mailContact;
        this.telephoneContact = telephoneContact;
        this.adresseContact = adresseContact;
        this.boitePostaleContact = boitePostaleContact;
        this.codePostalContact = codePostalContact;
        this.villeContact = villeContact;
        this.cedexContact = cedexContact;
    }
}
