package fr.abes.lnevent.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EtablissementCSV {
    public String IDEtablissement;
    public String SIRENEtalissement;
    public String NomEtablissement;
    public String TypeEtablissement;
    public String Adresse;
    public String Adresse2;
    public String BoitePostale;
    public String CodePostal;
    public String Cedex;
    public String Ville;
    public String ContactNom;
    public String ContactEmail;
    public String ContactTel;
    public String ListeAcces;
}
