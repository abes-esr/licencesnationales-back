package fr.abes.licencesnationales.core.entities.ip;

import com.github.jgonian.ipmath.Ipv6;
import com.github.jgonian.ipmath.Ipv6Range;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.ip.Ipv6RangeConverter;
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
@DiscriminatorValue("3")
public class IpV6 extends IpEntity implements Serializable {

    public static final List<Ipv6Range> reservedRange = Arrays.asList();

    @Convert(converter = Ipv6RangeConverter.class)
    @Lob
    private Ipv6Range ipRange;

    /**
     * CTOR d'une IP V6 à partir d'une châine de caractère normée et d'un commentaire
     *
     * @param ip           IP ou plage d'IP en chaîne de caractère selon la norme de l'application
     *                     XXXX:XXXX-XXXXX:XXXX:XXXX
     * @param commentaires Commentaire libre
     * @throws IpException Si l'IP ne peut pas être décodée ou si elle ne respecte pas les contraintes réseaux
     */
    public IpV6(String ip, String commentaires, StatutIpEntity statut) throws IpException {
        super(ip, commentaires, statut);
        try {
            // On transforme la chaîne de caractère normée en Objet Java
            this.ipRange = Ipv6Range.from(IpUtils.getIP(ip, IpType.IPV6, 1)).to(IpUtils.getIP(ip, IpType.IPV6, 2));
            this.checkIfReserved();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(Constant.ERROR_IPV6_INVALIDE + ip);
        }
    }

    /**
     * CTOR d'une IP V6 à partir d'un identifiant, d'une châine de caractère normée et d'un commentaire
     *
     * @param id           Identifiant de l'IP
     * @param ip           IP ou plage d'IP en chaîne de caractère selon la norme de l'application
     *                     XXXX:XXXX-XXXXX:XXXX:XXXX
     * @param commentaires Commentaire libre
     * @throws IpException Si l'IP ne peut pas être décodée ou si elle ne respecte pas les contraintes réseaux
     */
    public IpV6(Integer id, String ip, String commentaires, StatutIpEntity statut) throws IpException {
        super(id, ip, commentaires, statut);
        // On transforme la chaîne de caractère normée en Objet Java
        this.ipRange = Ipv6Range.from(IpUtils.getIP(ip, IpType.IPV6, 1)).to(IpUtils.getIP(ip, IpType.IPV6, 2));
        this.checkIfReserved();
    }

    /**
     * Vérifie si l'IP passée en paramètre fait partie de la plage d'IP
     *
     * @param ip Ipv6 à vérifier
     * @return Vrai si l'IP passée en paramètre fait partie de la plage d'IP, Faux sinon
     */
    public boolean contains(String ip) {
        Ipv6 inputIPAddress = Ipv6.of(ip);
        return ipRange.contains(inputIPAddress);
    }

    /**
     * Vérifie si la plage d'IP fait partie d'une plage d'IP réseau réservée
     *
     * @throws IpException Si la plage d'IP fait partie d'une plage d'IP réservée
     */
    private void checkIfReserved() throws IpException {
        Iterator<Ipv6Range> iter = IpV6.reservedRange.iterator();
        while (iter.hasNext()) {
            Ipv6Range candidate = iter.next();
            if (candidate.contains(this.ipRange)) {
                throw new IpException(String.format(Constant.ERROR_IP_RESERVEES,this.ipRange.toString(),candidate));
            }
        }
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
     * @return IpV6 de début de la plage
     */
    public Ipv6 getStart() {
        return this.ipRange.start();
    }

    /**
     * Retourne l'IP de fin de la plage
     *
     * @return IpV6 de fin de la plage
     */
    public Ipv6 getEnd() {
        return this.ipRange.end();
    }

    @Override
    public String formatRange() {
        if(this.isRange()){
            String[] digits =  this.getIp().split(":");
            StringBuilder output = new StringBuilder();
            for (int i = 0; i <digits.length; i++) {
                String[] octet =  digits[i].split("-");
                if(octet.length > 1) {
                    if(octet[0].equals(octet[1])){
                        output.append(octet[0]);
                    }else{
                        output.append(octet[0]).append("-").append(octet[1]);
                    }
                }else{
                    output.append(octet[0]);
                }
                if(i != digits.length-1){
                    output.append(":");
                }
            }
            return output.toString();
        }
        return "not range"; //TODO: lever une exception pour mauvaise utilisation de méthode ?
    }
}
