package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactEditeurDto {
    private String nomPrenom;
    private String mail;
    private String type;
}
