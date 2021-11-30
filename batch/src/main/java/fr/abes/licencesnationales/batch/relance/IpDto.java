package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter @Setter
public class IpDto implements Serializable {
    private Integer id;
    private String ip;
    private String nomEtab;
    private String mail;
    private String siren;

    public IpDto(IpEntity ip) {
        this.id = ip.getId();
        this.ip = ip.getIp();
        this.nomEtab = ip.getEtablissement().getNom();
        this.mail = ip.getEtablissement().getContact().getMail();
        this.siren = ip.getEtablissement().getSiren();
    }
}
