package fr.abes.lnevent.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpAjouteeDTO {


    @NotBlank
    private String siren;
    @NotBlank(message="Le type d'IP est obligatoire")
    private String typeIp;
    @NotBlank(message="L'IP est obligatoire")
    private String ip;
    @NotBlank
    private String typeAcces;
    private String commentaires;


}
