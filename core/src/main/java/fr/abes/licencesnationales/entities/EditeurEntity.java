package fr.abes.licencesnationales.entities;


import fr.abes.licencesnationales.converter.JpaConverterJson;
import fr.abes.licencesnationales.dto.editeur.ContactCommercialEditeurEventDto;
import fr.abes.licencesnationales.dto.editeur.ContactTechniqueEditeurEventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Editeur")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class EditeurEntity implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide")
    private String nomEditeur;

    @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres")
    private String identifiantEditeur;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresseEditeur;

    private Date dateCreation;

    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<String> groupesEtabRelies = new ArrayList<>();

    @OneToMany(mappedBy = "editeurEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities = new HashSet<>();

    @OneToMany(mappedBy = "editeurEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities = new HashSet<>();

    public EditeurEntity(Long id,
                         String nomEditeur,
                         String identifiantEditeur,
                         String adresseEditeur,
                         Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities,
                         Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities) {
        this.id = id;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        this.dateCreation = new Date();
        this.contactCommercialEditeurEntities = contactCommercialEditeurEntities;
        this.contactTechniqueEditeurEntities = contactTechniqueEditeurEntities;
    }
}
