package fr.abes.lnevent.repository.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "editeur")
public class EditeurRow {
    public EditeurRow(String id, String nom, String adresse, List<String> mailsPourBatch, List<String> mailPourInformation) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.mailsPourBatch = mailsPourBatch;
        this.mailPourInformation = mailPourInformation;
    }

    @Id
    public String id;

    public String nom;

    public String adresse;

    public List<String> mailsPourBatch;

    public List<String> mailPourInformation;
}
