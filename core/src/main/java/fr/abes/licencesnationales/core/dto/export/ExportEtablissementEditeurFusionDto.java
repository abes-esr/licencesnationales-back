package fr.abes.licencesnationales.core.dto.export;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ExportEtablissementEditeurFusionDto extends ExportEtablissementEditeurDto {
    private Set<String> sirensFusionnes;
}
