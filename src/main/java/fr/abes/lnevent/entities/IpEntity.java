package fr.abes.lnevent.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Ip")
@NoArgsConstructor
@Getter @Setter
public class IpEntity implements Serializable {

    public IpEntity(Long id, String ip, String typeAcces, String typeIp) {
        this.id = id;
        this.ip = ip;
        this.validee = false;
        this.dateCreation = new Date();
        this.typeAcces = typeAcces;
        this.typeIp=typeIp;

    }

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "ip_Sequence")
    @SequenceGenerator(name = "ip_Sequence", sequenceName = "IP_SEQ", allocationSize = 1)
    private Long id;

    private String ip;

    private boolean validee;

    private Date dateCreation;

    private Date dateModification;

    private String typeAcces;

    private String typeIp;
}
