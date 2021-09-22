package fr.abes.licencesnationales.core.entities.editeur;


import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Editeur")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    private Integer idEditeur;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide")
    private String nomEditeur;

    @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres")
    private String identifiantEditeur;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresseEditeur;

    private Date dateCreation;

    @JoinTable(name = "editeurs_types_etablissements", joinColumns = @JoinColumn(name = "ref_editeur"), inverseJoinColumns = @JoinColumn(name = "ref_type_etablissement"))
    @ManyToMany
    private Set<TypeEtablissementEntity> typeEtablissements;

    @OneToMany(targetEntity = ContactCommercialEditeurEntity.class, mappedBy = "editeurEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "DTYPE='commercial'")
    private Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities = new HashSet<>();

    @OneToMany(targetEntity = ContactTechniqueEditeurEntity.class, mappedBy = "editeurEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "DTYPE='technique'")
    private Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities = new HashSet<>();

    /**
     * CTOR d'un éditeur avec un identifiant
     *
     * @param idEditeur
     * @param nomEditeur
     * @param identifiantEditeur
     * @param adresseEditeur
     * @param dateCreation
     * @param contactCommercialEditeurEntities
     * @param contactTechniqueEditeurEntities
     */
    public EditeurEntity(Integer idEditeur,
                         String nomEditeur,
                         String identifiantEditeur,
                         String adresseEditeur,
                         Date dateCreation,
                         Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities,
                         Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities) {
        this.idEditeur = idEditeur;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        this.dateCreation = dateCreation;
        this.contactCommercialEditeurEntities = contactCommercialEditeurEntities;
        this.contactTechniqueEditeurEntities = contactTechniqueEditeurEntities;
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

        return (idEditeur != null && idEditeur.equals(((EditeurEntity) obj).idEditeur)) ||
                (idEditeur == null && nomEditeur.equals(((EditeurEntity) obj).nomEditeur));
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "EditeurEntity {" + "id=" + idEditeur + ", nom=" + nomEditeur + " }";
    }
}
