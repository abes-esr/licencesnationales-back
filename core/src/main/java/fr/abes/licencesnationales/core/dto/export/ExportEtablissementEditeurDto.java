package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExportEtablissementEditeurDto {
    protected String idEtablissement;
    protected String sirenEtablissement;
    protected String nomEtablissement;
    protected String typeEtablissement;
    protected String adresse;
    protected String boitePostale;
    protected String codePostal;
    protected String cedex;
    protected String ville;
    protected String nomContact;
    protected String mailContact;
    protected String telephoneContact;
    protected List<String> listeAcces = new ArrayList<>();

    public void ajouterAcces(String acces) {
        this.listeAcces.add(acces);
    }
}
