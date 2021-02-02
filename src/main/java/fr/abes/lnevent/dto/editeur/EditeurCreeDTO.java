package fr.abes.lnevent.dto.editeur;

import lombok.Getter;

import java.util.List;

@Getter
public class EditeurCreeDTO {
    private String nom;

    private String adresse;

    private List<String> mailPourBatch;

    private List<String> mailPourInformation;

}
