package fr.abes.licencesnationales.core.entities.etablissement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.abes.licencesnationales.core.constant.Constant.*;

@Entity
@Table(name = "Etablissement")
public class EtablissementEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etablissement_Sequence")
    @SequenceGenerator(name = "etablissement_Sequence", sequenceName = "ETABLISSEMENT_SEQ", allocationSize = 1)
    @Getter @Setter
    private Integer id;

    @NotBlank( message = Constant.ERROR_ETAB_NOM_OBLIGATOIRE)
    @Getter @Setter
    private String nom;

    @NotNull( message = Constant.ERROR_ETAB_SIREN_OBLIGATOIRE)
    @Column(name = "siren", unique = true)
    @Pattern(regexp = "^\\d{9}$", message = Constant.SIREN_DOIT_CONTENIR_9_CHIFFRES)
    @Getter @Setter
    private String siren;

    @Getter @Setter
    private Date dateCreation = new Date();

    @NotNull( message = Constant.ERROR_ETAB_TYPE_ETAB_OBLIGATOIRE)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ref_typeEtablissement")
    @Getter @Setter
    private TypeEtablissementEntity typeEtablissement;

    @Getter
    @Setter
    private boolean valide;

    @Getter
    @Setter
    private String idAbes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private ContactEntity contact;

    @OneToMany(mappedBy = "etablissement", cascade = CascadeType.ALL)
    @JsonIgnore
    @Getter
    @Setter
    private Set<IpEntity> ips = new HashSet<>();

    @Transient
    private String statut;

    @Deprecated
    public EtablissementEntity() {

    }


    /**
     * CTOR d'un établissement avec un identifiant
     *
     * @param id                Identifiant de l'établissement
     * @param nom               Nom de l'établissement
     * @param siren             Numéro SIRENE de l'établissement
     * @param typeEtablissement Type de l'établissement
     * @param idAbes            Identifiant ABES de l'établissement
     * @param contact           Contact de l'établissement
     */
    public EtablissementEntity(Integer id, String nom, String siren, TypeEtablissementEntity typeEtablissement, String idAbes, ContactEntity contact) {
        this.id = id;
        this.nom = nom;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;
        if (contact == null) {
            throw new IllegalArgumentException(Constant.CONTACT_OBLIGATOIRE);
        }
        this.contact = contact;
        this.valide = false;
    }

    /**
     * CTOR d'un établissement sans identifiant et sans éditeurs
     *
     * @param nom               Nom de l'établissement
     * @param siren             Numéro SIRENE de l'établissement
     * @param typeEtablissement Type de l'établissement
     * @param idAbes            Identifiant ABESZ de l'établissement
     * @param contact           Contact de l'établissement
     */
    public EtablissementEntity(String nom, String siren, TypeEtablissementEntity typeEtablissement, String idAbes, ContactEntity contact) {
        this.nom = nom;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;
        if (contact == null) {
            throw new IllegalArgumentException(Constant.CONTACT_OBLIGATOIRE);
        }
        this.contact = contact;
        this.valide = false;
    }

    public EtablissementEntity(String nom, String siren, ContactEntity contact) {
        this.nom = nom;
        this.siren = siren;
        this.contact = contact;
        this.valide = false;
    }

    public void ajouterIp(IpEntity ip) {
        ip.setEtablissement(this);
        this.ips.add(ip);
    }

    public void ajouterIps(Set<IpEntity> ips) {
        ips.forEach(ip -> ip.setEtablissement(this));
        this.ips.addAll(ips);
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

        return id != null && id.equals(((EtablissementEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "EtablissementEntity {" + "id=" + id + ", nom=" + nom + ", SIREN=" + siren + ", type=" + typeEtablissement + " }";
    }

    public String getStatut() {
        if (this.ips.isEmpty()) {
            return Constant.STATUT_ETAB_SANSIP;
        } else {
            Stream<IpEntity> ipSorted = this.ips.stream().collect(Collectors.toSet()).stream().sorted();
            String statut = "";
            for (IpEntity ip : ipSorted.collect(Collectors.toSet())) {
                switch (ip.getStatut().getIdStatut()) {
                    case STATUT_IP_NOUVELLE:
                        statut = STATUT_ETAB_EXAMINERIP;
                        break;
                    case STATUT_IP_ATTESTATION:
                        if (!statut.equals(STATUT_ETAB_EXAMINERIP))
                            statut = STATUT_ETAB_ATTENTEATTESTATION;
                        break;
                    default:
                        if (!statut.equals(STATUT_ETAB_EXAMINERIP) && !statut.equals(STATUT_ETAB_ATTENTEATTESTATION))
                            statut = STATUT_ETAB_IPOK;
                }
            }
            return statut;
        }
    }
}
