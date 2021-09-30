package fr.abes.licencesnationales.core.entities.editeur;


import fr.abes.licencesnationales.core.dto.ContactEditeurDto;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Editeur")
@NoArgsConstructor
@Getter
@Slf4j
public class EditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    private Integer idEditeur = 000;

    @NotNull
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide")
    private String nomEditeur = "nom editeur non renseigné";

    @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres")
    private String identifiantEditeur = "identifiant editeur non renseigné";

    @NotNull
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresseEditeur = "adresse editeur non renseignée";

    private Date dateCreation = new Date();

    @JoinTable(name = "editeurs_types_etablissements", joinColumns = @JoinColumn(name = "ref_editeur", nullable = false), inverseJoinColumns = @JoinColumn(name = "ref_type_etablissement"))
    @ManyToMany
    private Set<TypeEtablissementEntity> typeEtablissements = new HashSet<>();

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
     * @
     */
    public EditeurEntity(Integer idEditeur,
                         String nomEditeur,
                         String identifiantEditeur,
                         String adresseEditeur,
                         Date dateCreation,
                         Set<TypeEtablissementEntity> typeEtablissements) {
        this.idEditeur = idEditeur;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        this.dateCreation = dateCreation;
        this.typeEtablissements = typeEtablissements;
    }

    /**
     * CTOR d'un éditeur avec sans idEditeur
     *
     * @param nomEditeur
     * @param identifiantEditeur
     * @param adresseEditeur
     * @param dateCreation
     */
    public EditeurEntity(String nomEditeur,
                         String identifiantEditeur,
                         String adresseEditeur,
                         Date dateCreation,
                         Set<TypeEtablissementEntity> typeEtablissements) {
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        this.dateCreation = dateCreation;
        this.typeEtablissements = typeEtablissements;
    }

    public void ajouterContact(ContactEditeurEntity contact){
        contact.setEditeurEntity(this);
        if(contact instanceof ContactCommercialEditeurEntity)
            this.contactCommercialEditeurEntities.add((ContactCommercialEditeurEntity) contact);
        else if(contact instanceof ContactTechniqueEditeurEntity)
            this.contactTechniqueEditeurEntities.add((ContactTechniqueEditeurEntity) contact);
        else {
            throw new NotImplementedException("Le contact doit forcément être commercial ou technique");
        }
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

    public void setIdEditeur(Integer idEditeur) {
        this.idEditeur = idEditeur;
    }

    public void setNomEditeur(String nomEditeur) {
        this.nomEditeur = nomEditeur;
    }

    public void setIdentifiantEditeur(String identifiantEditeur) {
        this.identifiantEditeur = identifiantEditeur;
    }

    public void setAdresseEditeur(String adresseEditeur) {
        this.adresseEditeur = adresseEditeur;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setTypeEtablissements(Set<TypeEtablissementEntity> typeEtablissements) {
        this.typeEtablissements = typeEtablissements;
    }
}
