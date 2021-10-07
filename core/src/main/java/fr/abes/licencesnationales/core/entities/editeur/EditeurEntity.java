package fr.abes.licencesnationales.core.entities.editeur;


import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Editeur")
@Getter @Setter
@Slf4j
public class EditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    private Integer id;

    @NotNull
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide")
    private String nom;

    @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres")
    private String identifiant = "identifiant editeur non renseigné";

    @NotNull
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresse;

    private Date dateCreation = new Date();

    @JoinTable(name = "editeurs_types_etablissements",
            joinColumns = @JoinColumn(name = "ref_editeur", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "ref_type_etablissement"))
    @ManyToMany
    private Set<TypeEtablissementEntity> typeEtablissements = new HashSet<>();

    @OneToMany(targetEntity = ContactCommercialEditeurEntity.class, mappedBy = "editeur", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "DTYPE='commercial'")
    private Set<ContactCommercialEditeurEntity> contactsCommerciaux = new HashSet<>();

    @OneToMany(targetEntity = ContactTechniqueEditeurEntity.class, mappedBy = "editeur", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "DTYPE='technique'")
    private Set<ContactTechniqueEditeurEntity> contactsTechniques = new HashSet<>();

    @Deprecated
    public EditeurEntity() {

    }

    /**
     * CTOR d'un éditeur avec un identifiant
     *
     * @param id
     * @param nom
     * @param identifiant
     * @param adresse
     * @
     */
    public EditeurEntity(Integer id,
                         String nom,
                         String identifiant,
                         String adresse,
                         Set<TypeEtablissementEntity> typeEtablissements) {
        this.id = id;
        this.nom = nom;
        this.identifiant = identifiant;
        this.adresse = adresse;
        this.typeEtablissements = typeEtablissements;
    }

    /**
     * CTOR d'un éditeur avec sans idEditeur
     *
     * @param nom
     * @param identifiant
     * @param adresse
     */
    public EditeurEntity(String nom,
                         String identifiant,
                         String adresse,
                         Set<TypeEtablissementEntity> typeEtablissements) {
        this.nom = nom;
        this.identifiant = identifiant;
        this.adresse = adresse;
        this.dateCreation = new Date();
        this.typeEtablissements = typeEtablissements;
    }

    public void ajouterContact(ContactEditeurEntity contact){
        contact.setEditeur(this);
        if(contact instanceof ContactCommercialEditeurEntity)
            this.contactsCommerciaux.add((ContactCommercialEditeurEntity) contact);
        else if(contact instanceof ContactTechniqueEditeurEntity)
            this.contactsTechniques.add((ContactTechniqueEditeurEntity) contact);
        else {
            throw new NotImplementedException("Le contact doit forcément être commercial ou technique");
        }
    }

    public void ajouterContactsCommerciaux(Set<ContactCommercialEditeurEntity> contacts) {
        contacts.forEach(c -> ajouterContact(c));
    }

    public void ajouterContactsTechniques(Set<ContactTechniqueEditeurEntity> contacts) {
        contacts.forEach(c -> ajouterContact(c));
    }

    public void ajouterTypeEtablissement(TypeEtablissementEntity type) {
        this.typeEtablissements.add(type);
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

        return (id != null && id.equals(((EditeurEntity) obj).id)) ||
                (id == null && nom.equals(((EditeurEntity) obj).nom));
    }

    @Override
    public int hashCode() {
        return 2021;
    }

    @Override
    public String toString() {
        return "EditeurEntity {" + "id=" + id + ", nom=" + nom + " }";
    }

    public void setId(Integer idEditeur) {
        this.id = idEditeur;
    }

    public void setNom(String nomEditeur) {
        this.nom = nomEditeur;
    }

    public void setIdentifiant(String identifiantEditeur) {
        this.identifiant = identifiantEditeur;
    }

    public void setAdresse(String adresseEditeur) {
        this.adresse = adresseEditeur;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setTypeEtablissements(Set<TypeEtablissementEntity> typeEtablissements) {
        this.typeEtablissements = typeEtablissements;
    }
}
