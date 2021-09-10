package fr.abes.licencesnationales.core.entities.ip;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import fr.abes.licencesnationales.core.converter.Ipv4RangeConverter;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.utils.IpUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("2")
public class IpV4 extends IpEntity implements Serializable {

    public static final List<Ipv4Range> reservedRange = Arrays.asList(
            Ipv4Range.from("10.0.0.0").to("10.255.255.254"),
            Ipv4Range.from("192.168.0.0").to("192.168.254.254"),
            Ipv4Range.from("172.0.0.0").to("172.31.254.254"),
            Ipv4Range.from("127.0.0.1").to("127.0.0.1"));


    @Convert(converter= Ipv4RangeConverter.class)
    @Lob
    private Ipv4Range ipRange;

    /**
     * @param ip           Ip ou la range d'IP sous forme de chaîne de caractère
     * @param commentaires
     * @throws IpException si l'IP fait parti des IP réservées
     */
    public IpV4(String ip, String commentaires) throws IpException {
        super(ip, commentaires);
        // On transforme la chaîne de caractère normée en Objet Java
        this.ipRange = Ipv4Range.from(IpUtils.getIP(ip,IpType.IPV4,1)).to(IpUtils.getIP(ip,IpType.IPV4,2));
        this.checkIfReserved();
    }

    /**
     *
     * @param id
     * @param ip
     * @param commentaires
     * @throws IpException
     */
    public IpV4(Long id, String ip, String commentaires) throws IpException {
        super(id,ip, commentaires);
        // On transforme la chaîne de caractère normée en Objet Java
        this.ipRange = Ipv4Range.from(IpUtils.getIP(ip,IpType.IPV4,1)).to(IpUtils.getIP(ip,IpType.IPV4,2));
        this.checkIfReserved();
    }

    private boolean checkIfReserved() throws IpException {
        Iterator<Ipv4Range> iter = IpV4.reservedRange.iterator();
        while (iter.hasNext()) {
            Ipv4Range candidate = iter.next();
            if (candidate.contains(this.ipRange)) {
                throw new IpException(this.ipRange.toString() + " est inclus dans les IP réservées " + candidate.toString());
            }
        }
        return true;
    }

    public boolean contains(String ip) {
        Ipv4 inputIPAddress = Ipv4.of(ip);
        return ipRange.contains(inputIPAddress);
    }

    public boolean isRange() {
        return !ipRange.start().equals(ipRange.end());
    }

    public Ipv4 getStart() {
        return this.ipRange.start();
    }

    public Ipv4 getEnd() {
        return this.ipRange.end();
    }

}
