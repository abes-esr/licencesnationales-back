package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExportEtablissementAdminDto {
    private String idAbes;
    private String siren;
    private String nom;
    private String typeEtablissement;
    private String adresse;
    private String ville;
    private String telephone;
    private String nomPrenomContact;
    private String mailContact;
    private List<String> ips = new ArrayList<>();

    public void ajouterIp(String ip) {
        this.ips.add(ip);
    }
}
