package fr.abes.lnevent.dto.editeur;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

@Getter
public class EditeurModifieDTO extends EditeurDTO{

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide")
    private String nomEditeur;

    @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres")
    private String identifiantEditeur;

    private List<String> groupesEtabRelies;

    @NotBlank
    @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide")
    private String adresseEditeur;

    public Set<ContactCommercialEditeurDTO> listeContactCommercialEditeurDTO;

    public Set<ContactTechniqueEditeurDTO> listeContactTechniqueEditeurDTO;

    public EditeurModifieDTO(@NotBlank @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide") String nomEditeur, @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres") String identifiantEditeur, List<String> groupesEtabRelies, @NotBlank @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide") String adresseEditeur, Set<ContactCommercialEditeurDTO> listeContactCommercialEditeurDTO, Set<ContactTechniqueEditeurDTO> listeContactTechniqueEditeurDTO, @NotBlank @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "Le nom de l'éditeur fourni n'est pas valide") String nomEditeur1, @Pattern(regexp = "^[0-9]*$", message = "L'identifiant éditeur est uniquement composé de chiffres") String identifiantEditeur1, List<String> groupesEtabRelies1, @NotBlank @Pattern(regexp = "^([0-9A-Za-z'àâéèêôùûçÀÂÉÈÔÙÛÇ,\\s-]{5,80})$", message = "L'adresse postale fournie n'est pas valide") String adresseEditeur1, Set<ContactCommercialEditeurDTO> listeContactCommercialEditeurDTO1, Set<ContactTechniqueEditeurDTO> listeContactTechniqueEditeurDTO1) {
        super(nomEditeur, identifiantEditeur, groupesEtabRelies, adresseEditeur, listeContactCommercialEditeurDTO, listeContactTechniqueEditeurDTO);
        this.nomEditeur = nomEditeur1;
        this.identifiantEditeur = identifiantEditeur1;
        this.groupesEtabRelies = groupesEtabRelies1;
        this.adresseEditeur = adresseEditeur1;
        this.listeContactCommercialEditeurDTO = listeContactCommercialEditeurDTO1;
        this.listeContactTechniqueEditeurDTO = listeContactTechniqueEditeurDTO1;
    }
}
