package fr.abes.licencesnationales.core.entities.ip;

import com.github.jgonian.ipmath.Ipv6;
import com.github.jgonian.ipmath.Ipv6Range;
import fr.abes.licencesnationales.core.converter.Ipv6RangeConverter;
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
@DiscriminatorValue("3")
public class IpV6 extends IpEntity implements Serializable {

    public static final List<Ipv6Range> reservedRange = Arrays.asList();

    @Convert(converter= Ipv6RangeConverter.class)
    @Lob
    private Ipv6Range ipRange;

    /**
     * @param ip           Ip ou la range d'IP sous forme de chaîne de caractère
     * @param commentaires
     * @throws IpException si l'IP fait parti des IP réservées
     */
    public IpV6(String ip, String commentaires) throws IpException {
        super(ip, commentaires);
        // On transforme la chaîne de caractère normée en Objet Java
        this.ipRange = Ipv6Range.from(IpUtils.getIP(ip,IpType.IPV6,1)).to(IpUtils.getIP(ip,IpType.IPV6,2));
        this.checkIfReserved();
    }

    /**
     *
     * @param id
     * @param ip
     * @param commentaires
     * @throws IpException
     */
    public IpV6(Long id, String ip, String commentaires) throws IpException {
        super(id, ip, commentaires);
        // On transforme la chaîne de caractère normée en Objet Java
        this.ipRange = Ipv6Range.from(IpUtils.getIP(ip,IpType.IPV6,1)).to(IpUtils.getIP(ip,IpType.IPV6,2));
        this.checkIfReserved();
    }

    public boolean contains(String ip) {
        Ipv6 inputIPAddress = Ipv6.of(ip);
        return ipRange.contains(inputIPAddress);
    }

    private boolean checkIfReserved() throws IpException {
        Iterator<Ipv6Range> iter = IpV6.reservedRange.iterator();
        while (iter.hasNext()) {
            Ipv6Range candidate = iter.next();
            if (candidate.contains(this.ipRange)) {
                throw new IpException(this.ipRange.toString() + " est inclus dans les IP réservées " + candidate.toString());
            }
        }
        return true;
    }

    public boolean isRange() {
        return !ipRange.start().equals(ipRange.end());
    }

    public Ipv6 getStart() {
        return this.ipRange.start();
    }

    public Ipv6 getEnd() {
        return this.ipRange.end();
    }

}
