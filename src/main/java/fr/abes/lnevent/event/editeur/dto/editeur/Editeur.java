package fr.abes.lnevent.event.editeur.dto.editeur;

import lombok.Getter;

import java.util.List;

@Getter
public class Editeur {
    private String nom;

    private String adresse;

    private List<String> mailPourBatch;

    private List<String> mailPourInformation;
}
