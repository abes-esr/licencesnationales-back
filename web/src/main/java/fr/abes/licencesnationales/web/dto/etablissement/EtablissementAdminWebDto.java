package fr.abes.licencesnationales.web.dto.etablissement;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EtablissementAdminWebDto extends EtablissementWebDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("nom")
    private String name;

    @JsonProperty("siren")
    private String siren;

    @JsonProperty("typeEtablissement")
    private String typeEtablissement;

    @JsonProperty("idAbes")
    private String idAbes;

}
