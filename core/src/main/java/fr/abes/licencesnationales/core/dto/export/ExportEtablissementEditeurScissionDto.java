package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportEtablissementEditeurScissionDto extends ExportEtablissementEditeurDto{
    private String sirenScinde;
}
