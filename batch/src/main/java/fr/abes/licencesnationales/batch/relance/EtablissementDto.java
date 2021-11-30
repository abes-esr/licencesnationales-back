package fr.abes.licencesnationales.batch.relance;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EtablissementDto {
    private String nomEtab;
    private String siren;
    private String email;
    private List<String> ipsAttestation;
    private List<String> ipsSupprimees;

    public EtablissementDto(String nomEtab, String siren, String email) {
        this.nomEtab = nomEtab;
        this.siren = siren;
        this.email = email;
        this.ipsAttestation = new ArrayList<>();
        this.ipsSupprimees = new ArrayList<>();
    }

    public void ajouterIpAttestation(String ip) {
        this.ipsAttestation.add(ip);
    }

    public void ajouterIpSupprimee(String ip) {
        this.ipsSupprimees.add(ip);
    }
}
