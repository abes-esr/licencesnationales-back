package fr.abes.licencesnationales.core.event.etablissement;


import fr.abes.licencesnationales.core.event.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class EtablissementFusionneEvent extends Event {
    private String nom;
    private String siren;
    private String typeEtablissement;
    private String idAbes;
    private String nomContact;
    private String prenomContact;
    private String adresseContact;
    private String boitePostaleContact;
    private String codePostalContact;
    private String villeContact;
    private String cedexContact;
    private String telephoneContact;
    private String mailContact;
    private String motDePasse;
    private String roleContact;
    private ArrayList<String> sirenFusionne;

    public EtablissementFusionneEvent(Object source, String nom, String siren, String typeEtablissement, String idAbes, String nomContact, String prenomContact,
                                      String adresseContact, String boitePostaleContact, String codePostalContact, String villeContact, String cedexContact,
                                      String telephoneContact, String mailContact, String motDePasse, String roleContact, ArrayList<String> sirenFusionne) {
        super(source);
        this.nom = nom;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;
        this.nomContact = nomContact;
        this.prenomContact = prenomContact;
        this.adresseContact = adresseContact;
        this.boitePostaleContact = boitePostaleContact;
        this.codePostalContact = codePostalContact;
        this.villeContact = villeContact;
        this.cedexContact = cedexContact;
        this.telephoneContact = telephoneContact;
        this.mailContact = mailContact;
        this.motDePasse = motDePasse;
        this.roleContact = roleContact;
        this.sirenFusionne = sirenFusionne;
    }

    public EtablissementFusionneEvent(Object source) {
        super(source);
    }
}
