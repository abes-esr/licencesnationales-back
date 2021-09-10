package fr.abes.licencesnationales.core.entities.ip;

import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER,
columnDefinition = "SMALLINT")
@DiscriminatorValue("1")
@Table(name = "Ip")
@NoArgsConstructor
@Getter
@Setter
public abstract class IpEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ip_Sequence")
    @SequenceGenerator(name = "ip_Sequence", sequenceName = "IP_SEQ", allocationSize = 1)
    private Long id;

    /**
     * L'IP est stockée sous forme de chaîne de caractère pour les bessoins
     * de retro-compatibilité avec l'ancienne version de l'application
     */
    @NotBlank(message = "L'IP est obligatoire")
    private String ip;

    /**
     * Booléen si l'IP est validée par les administrateurs
     */
    private boolean validee;

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

    @ManyToOne(targetEntity = EtablissementEntity.class, optional = false)
    private EtablissementEntity etablissement;

    /**
     * CTOR d'une IP générique sans identifiant connu
     *
     * @param ip
     * @param commentaires
     */
    public IpEntity(String ip, String commentaires) throws IpException {
        if (ip==null || ip.isEmpty()) {
            throw new IpException("Ip ne peut pas être nulle");
        }

        this.ip = ip;
        this.validee = false;
        this.dateCreation = new Date();
        this.commentaires = commentaires;
    }

    /**
     * CTOR d'une IP générique avec un identifiant connu
     * @param id
     * @param ip
     * @param commentaires
     * @throws IpException
     */
    public IpEntity(Long id, String ip, String commentaires) throws IpException {

        this.id = id;

        if (ip.isEmpty()) {
            throw new IpException("Ip ne peut pas être nulle");
        }

        this.ip = ip;
        this.validee = false;
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
