package fr.abes.licencesnationales.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Set;

@Entity
@Table(name = "Etablissement")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EtablissementEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "etablissement_Sequence")
    @SequenceGenerator(name = "etablissement_Sequence", sequenceName = "ETABLISSEMENT_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom d'établissement fourni n'est pas valide")
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir 9 chiffres")
    private String siren;

    private Date dateCreation;

    @NotBlank
    private String typeEtablissement;

    private boolean valide;

    private String idAbes;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ContactEntity contact;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnore
    private Set<IpEntity> ips = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "etablissement_editeur",
            joinColumns = @JoinColumn(name = "etablissement_id"),
            inverseJoinColumns = @JoinColumn(name = "editeur_id"))
    private Set<EditeurEntity> editeurs = new HashSet<>();



    public EtablissementEntity(Long id, String name, String siren, String typeEtablissement, String idAbes, ContactEntity contact, Set<EditeurEntity> editeurs) {
        this.id = id;
        this.name = name;
        this.siren = siren;
        this.dateCreation = new Date();
        this.typeEtablissement = typeEtablissement;
        this.idAbes = idAbes;
        this.contact = contact;
        this.editeurs = editeurs;
    }


}
