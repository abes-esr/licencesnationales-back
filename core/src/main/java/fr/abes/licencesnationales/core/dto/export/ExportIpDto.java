package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExportIpDto {
    private String ip;
    private String type;
    private String dateCreation;
    private String dateModificationStatut;
    private String statut;
    private String commentaire;
}