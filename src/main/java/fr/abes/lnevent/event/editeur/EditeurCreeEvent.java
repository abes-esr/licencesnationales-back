package fr.abes.lnevent.event.editeur;

import fr.abes.lnevent.event.Event;
import lombok.Getter;

import java.util.List;

@Getter
public class EditeurCreeEvent extends Event {

    public EditeurCreeEvent(Object source, String nom, String adresse, List<String> mailPourBatch, List<String> mailPourInformation) {
        super(source);
        this.nom = nom;
        this.adresse = adresse;
        this.mailPourBatch = mailPourBatch;
        this.mailPourInformation = mailPourInformation;
    }

    private String nom;

    private String adresse;

    private List<String> mailPourBatch;

    private List<String> mailPourInformation;
}
