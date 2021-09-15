package fr.abes.licencesnationales.web.dto.ip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpModifieeWebDto {
    protected Integer id;
    protected String siren;
    protected String ip;
    protected boolean validee;
    protected String typeAcces;
    protected String typeIp;
    protected String commentaires;
}
