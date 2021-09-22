package fr.abes.licencesnationales.core.entities.etablissement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Etablissement")
@Setter
@Getter
public class EtablissementEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etablissement_Sequence")
    @SequenceGenerator(name = "etablissement_Sequence", sequenceName = "ETABLISSEMENT_SEQ", allocationSize = 1)
    private Integer id;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom d'établissement fourni n'est pas valide")
    private String name;

    @NotBlank
    @Column(name = "siren", unique = true)
    @Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir 9 chiffres")
    private String siren;

    private Date dateCreation = new Date();

    @NotBlank
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ref_typeEtablissement")
    private TypeEtablissementEntity typeEtablissement;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ref_statut")
    private StatutEtablissementEntity statut;

    private String idAbes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private ContactEntity contact;

    @OneToMany(mappedBy = "etablissement", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnore
    private Set<IpEntity> ips = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "etablissement_editeur",
            joinColumns = @JoinColumn(name = "etablissement_id"),
            inverseJoinColumns = @JoinColumn(name = "editeur_id"))
    private Set<EditeurEntity> editeurs = new HashSet<>();

    @Deprecated
    public EtablissementEntity() {

    }


    /**
     * CTOR d'un établissement avec un identifiant
     *
     * @param id                Identifiant de l'établissement
     * @param name              Nom de l'établissement
     * @param siren             Numéro SIRENE de l'établissement
     * @param typeEtablissement Type de l'établissement
     * @param idAbes            Identifiant ABES de l'établissement
     * @param contact           Contact de l'établissement
     */
    public EtablissementEntity(Integer id, String name, String siren, TypeEtablissementEntity typeEtablissement, String idAbes, ContactEntity contact) {
        this.id = id;
        this.name = name;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;

        if (contact == null) {
            throw new IllegalArgumentException("Le contact est obligatoire");
        }
        this.contact = contact;
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
        this.name = nom;
        this.siren = siren;
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;
        if (contact == null) {
            throw new IllegalArgumentException("Le contact est obligatoire");
        }
        this.contact = contact;
    }

    public void ajouterEditeur(EditeurEntity editeur) {
        this.editeurs.add(editeur);
    }

    public void ajouterIp(IpEntity ip) {
        this.ips.add(ip);
    }

    public void ajouterIps(Set<IpEntity> ips) { this.ips.addAll(ips); }

    public void ajouterEditeurs(Set<EditeurEntity> editeurs) { this.editeurs.addAll(editeurs); }

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
        return "EtablissementEntity {" + "id=" + id + ", nom=" + name + ", SIRENE=" + siren + ", type=" + typeEtablissement + " }";
    }
}
