package fr.abes.licencesnationales.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class EtablissementCSV {
    private String IDEtablissement;
    private String SIRENEtalissement;
    private String NomEtablissement;
    private String TypeEtablissement;
    private String Adresse;
    private String Adresse2;
    private String BoitePostale;
    private String CodePostal;
    private String Cedex;
    private String Ville;
    private String ContactNom;
    private String ContactEmail;
    private String ContactTel;
    private String ListeAcces;
}
