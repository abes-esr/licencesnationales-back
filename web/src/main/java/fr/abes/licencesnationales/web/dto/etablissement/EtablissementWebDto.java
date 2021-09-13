package fr.abes.licencesnationales.web.dto.etablissement;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.abes.licencesnationales.web.dto.ContactWebDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Représente un exemplaire de Notice au format JSON de l'API
 */
@Getter @Setter
public class EtablissementWebDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nomEtab")
    private String name;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("typeEtablissement")
    private String typeEtablissement;

    @JsonProperty("idAbes")
    private String idAbes;

    @JsonProperty("contact")
    private ContactWebDto contact;

}
