package fr.abes.licencesnationales.core.entities.ip;

import com.github.jgonian.ipmath.AbstractIpRange;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER,
        columnDefinition = "SMALLINT")
@Table(name = "Ip")
@NoArgsConstructor
@Getter
@Setter
public abstract class IpEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ip_Sequence")
    @SequenceGenerator(name = "ip_Sequence", sequenceName = "IP_SEQ", allocationSize = 1)
    private Integer id;

    /**
     * L'IP est stockée sous forme de chaîne de caractère pour les bessoins
     * de retro-compatibilité avec l'ancienne version de l'application
     */
    @NotNull(message = "L'IP est obligatoire")
    private String ip;

    /**
     * Date de création de l'adresse IP
     */
    private Date dateCreation;

    /**
     * Date de la dernière modification de l'adresse IP
     */
    private Date dateModification;

    /**
     * Commentaire libre
     */
    private String commentaires;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_etablissement")
    private EtablissementEntity etablissement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_statut")
    private StatutIpEntity statut;


    /**
     * CTOR d'une IP générique sans identifiant connu
     *
     * @param ip           IP ou plage d'IP en chaîne de caractère selon la norme de l'application
     *                     XXX.XX-XX.XXX.XXX
     * @param commentaires Commentaire libre
     * @throws IpException Si l'IP ne peut pas être décodée ou si elle ne respecte pas les contraintes réseaux
     */
    public IpEntity(String ip, String commentaires) throws IpException {
        if (ip == null || ip.isEmpty()) {
            throw new IpException("Ip ne peut pas être nulle");
        }

        this.ip = ip;
        this.dateCreation = new Date();
        this.commentaires = commentaires;
    }

    /**
     * CTOR d'une IP générique avec un identifiant connu
     *
     * @param id           Identifiant de l'IP
     * @param ip           IP ou plage d'IP en chaîne de caractère selon la norme de l'application
     *                     XXX.XX-XX.XXX.XXX
     * @param commentaires Commentaire libre
     * @throws IpException Si l'IP ne peut pas être décodée ou si elle ne respecte pas les contraintes réseaux
     */
    public IpEntity(Integer id, String ip, String commentaires) throws IpException {

        this.id = id;

        if (ip.isEmpty()) {
            throw new IpException("Ip ne peut pas être nulle");
        }

        this.ip = ip;
        this.dateCreation = new Date();
        this.commentaires = commentaires;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return id != null && id.equals(((IpEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return 2020;
    }

    @Override
    public String toString() {
        return "IpEntity {" + "id=" + id + "}";
    }

}
