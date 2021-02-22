package fr.abes.lnevent.entities;


import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Ip")
@NoArgsConstructor
public class IpEntity {

    public IpEntity(Long id, String ip, String siren) {
        this.id = id;
        this.ip = ip;
        this.siren = siren;
        this.validee = false;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "ip_Sequence")
    @SequenceGenerator(name = "ip_Sequence", sequenceName = "IP_SEQ", allocationSize = 1)
    public Long id;

    public String ip;

    public String siren;

    public boolean validee;
}
