package fr.abes.licencesnationales.core.entities.ip;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.ip.Ipv4RangeConverter;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.utils.IpUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
            Ipv4Range.from("10.0.0.0").to("10.255.255.255"),
            Ipv4Range.from("192.168.0.0").to("192.168.255.255"),
            Ipv4Range.from("172.16.0.0").to("172.31.255.255"),
            Ipv4Range.from("127.0.0.1").to("127.0.0.1"),
            Ipv4Range.from("0.0.0.0").to("0.0.0.0"),
            Ipv4Range.from("255.255.255.255").to("255.255.255.255"));


    @Convert(converter = Ipv4RangeConverter.class)
    @Lob
    private Ipv4Range ipRange;

    /**
     * CTOR d'une IP V4 à partir d'une châine de caractère normée et d'un commentaire
     *
     * @param ip           IP ou plage d'IP en chaîne de caractère selon la norme de l'application
     *                     XXX.XX-XX.XXX.XXX
     * @param commentaires Commentaire libre
     * @throws IpException Si l'IP ne peut pas être décodée ou si elle ne respecte pas les contraintes réseaux
     */
    public IpV4(String ip, String commentaires, StatutIpEntity statut) throws IpException {
        super(ip, commentaires, statut);
        try {
            // On transforme la chaîne de caractère normée en Objet Java
            this.ipRange = Ipv4Range.from(IpUtils.getIP(ip, IpType.IPV4, 1)).to(IpUtils.getIP(ip, IpType.IPV4, 2));
            this.checkIfReserved();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(Constant.ERROR_IPV4_INVALIDE + ip);
        }
    }

    /**
     * CTOR d'une IP V4 à partir d'un identifiant, d'une châine de caractère normée et d'un commentaire
     *
     * @param id           Identifiant de l'IP
     * @param ip           IP ou plage d'IP en chaîne de caractère selon la norme de l'application
     *                     XXX.XX-XX.XXX.XXX
     * @param commentaires Commentaire libre
     * @throws IpException Si l'IP ne peut pas être décodée ou si elle ne respecte pas les contraintes réseaux
     */
    public IpV4(Integer id, String ip, String commentaires, StatutIpEntity statut) throws IpException {
        super(id, ip, commentaires, statut);
        // On transforme la chaîne de caractère normée en Objet Java
        this.ipRange = Ipv4Range.from(IpUtils.getIP(ip, IpType.IPV4, 1)).to(IpUtils.getIP(ip, IpType.IPV4, 2));
        this.checkIfReserved();
    }

    /**
     * Vérifie si la plage d'IP fait partie d'une plage d'IP réseau réservée
     *
     * @throws IpException Si la plage d'IP fait partie d'une plage d'IP réservée
     */
    private void checkIfReserved() throws IpException {
        Iterator<Ipv4Range> iter = IpV4.reservedRange.iterator();
        while (iter.hasNext()) {
            Ipv4Range candidate = iter.next();
            if (candidate.contains(this.ipRange)) {
                throw new IpException(String.format(Constant.ERROR_IP_RESERVEES,this.ipRange.toString(),candidate));
            }
        }
    }

    /**
     * Vérifie si l'IP passée en paramètre fait partie de la plage d'IP
     *
     * @param ip Ipv4 à vérifier
     * @return Vrai si l'IP passée en paramètre fait partie de la plage d'IP, Faux sinon
     */
    public boolean contains(Ipv4 ip) {
        return ipRange.contains(ip);
    }

    /**
     * Vérifie si la plage d'IP est une plage ou un IP seule
     *
     * @return Vrai si c'est une plage d'IP, Faux sinon
     */
    public boolean isRange() {
        return !ipRange.start().equals(ipRange.end());
    }

    /**
     * Retourne l'IP de début de la plage
     *
     * @return IpV4 de début de la plage
     */
    public Ipv4 getStart() {
        return this.ipRange.start();
    }

    /**
     * Retourne l'IP de fin de la plage
     *
     * @return IpV4 de fin de la plage
     */
    public Ipv4 getEnd() {
        return this.ipRange.end();
    }

    //    194.57.116-116.1-255
    @Override
    public String formatRange(){
        String[] octetStart = this.getStart().toString().split("\\.");
        String[] octetEnd = this.getEnd().toString().split("\\.");
        if(this.isRange()){
            return  octetStart[0]+"." + octetStart[1]+"."+
                    octetStart[2] + "-" + octetEnd[2]+"."+
                    octetStart[3] + "-" + octetEnd[3];
        }
        return "not range"; //TODO: lever une exception pour mauvaise utilisation de méthode ?
    }
}
