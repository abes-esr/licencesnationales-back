package fr.abes.licencesnationales.entities;


import fr.abes.licencesnationales.converter.JpaConverterJson;
import fr.abes.licencesnationales.dto.editeur.ContactCommercialEditeurDTO;
import fr.abes.licencesnationales.dto.editeur.ContactTechniqueEditeurDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Editeur")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class EditeurEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "editeur_Sequence")
    @SequenceGenerator(name = "editeur_Sequence", sequenceName = "EDITEUR_SEQ", allocationSize = 1)
    private Long id;

    private String nomEditeur;

    private String identifiantEditeur;

    private String adresseEditeur;

    private Date dateCreation;


    /*@Lob
    @Convert(fr.abes.licencesnationales.converter = JpaConverterJson.class)
    private List<String> mailsPourBatch;

    @Lob
    @Convert(fr.abes.licencesnationales.converter = JpaConverterJson.class)
    private List<String> mailPourInformation;


     */
    @Lob
    @Convert(converter = JpaConverterJson.class)
    private List<String> groupesEtabRelies = new ArrayList<>();

    /*@ManyToMany(mappedBy = "editeurs")
    private Set<EtablissementEntity> etablissements = new HashSet<>();*/


    @OneToMany(mappedBy = "editeurEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities;

    @OneToMany(mappedBy = "editeurEntity", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities;



    public EditeurEntity(Long id,
                         String nomEditeur,
                         String identifiantEditeur,
                         String adresseEditeur, /*List<String> mailsPourBatch, List<String> mailPourInformation, Set<EtablissementEntity> etablissements,*/
                         Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities,
                         Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities) {
        this.id = id;
        this.nomEditeur = nomEditeur;
        this.identifiantEditeur = identifiantEditeur;
        this.adresseEditeur = adresseEditeur;
        this.dateCreation = new Date();
        //this.mailsPourBatch = mailsPourBatch;
        //this.mailPourInformation = mailPourInformation;
        //this.etablissements = etablissements;
        this.contactCommercialEditeurEntities = contactCommercialEditeurEntities;
        this.contactTechniqueEditeurEntities = contactTechniqueEditeurEntities;
    }


    public EditeurEntity(Object o, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies, String adresseEditeur, Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities, Set<ContactTechniqueEditeurEntity> contactTechniqueEditeurEntities) {
    }

    public EditeurEntity(Object o, String nomEditeur, String identifiantEditeur, String adresseEditeur, Set<ContactCommercialEditeurEntity> contactCommercialEditeurEntities) {
    }


    public EditeurEntity(Long id, String nomEditeur, String identifiantEditeur, List<String> groupesEtabRelies, String adresseEditeur, Set<ContactCommercialEditeurDTO> contactCommercialEditeurDTOS, Set<ContactTechniqueEditeurDTO> contactTechniqueEditeurDTOS) {
    }
}
