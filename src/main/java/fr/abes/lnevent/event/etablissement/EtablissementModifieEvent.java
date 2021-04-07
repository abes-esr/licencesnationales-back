package fr.abes.lnevent.event.etablissement;

import fr.abes.lnevent.dto.etablissement.EtablissementDTO;
import fr.abes.lnevent.event.Event;
import lombok.Getter;

@Getter
public class EtablissementModifieEvent extends Event {

    public EtablissementModifieEvent(Object source, String siren, String nomContact, String adresseContact, String mailContact, String telephoneContact) {
        super(source);
        this.siren = siren;
        this.nomContact = nomContact;
        this.adresseContact = adresseContact;
        this.mailContact = mailContact;
        this.telephoneContact = telephoneContact;
    }

    private String siren;

    private String nomContact;
    private String adresseContact;
    private String mailContact;
    private String telephoneContact;
}
