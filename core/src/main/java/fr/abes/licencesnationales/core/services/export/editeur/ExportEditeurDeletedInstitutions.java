package fr.abes.licencesnationales.core.services.export.editeur;

import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurDto;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import fr.abes.licencesnationales.core.services.ExportEditeurService;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.stripAccents;

@Service
public class ExportEditeurDeletedInstitutions extends ExportEditeurService<ExportEtablissementEditeurDto> {
    @Autowired
    private UtilsMapper mapper;
    @Autowired
    private EtablissementService etablissementService;
    @Autowired
    private DateEnvoiEditeurRepository dateEnvoiEditeurRepository;
    @Autowired
    private EventService eventService;

    @Override
    protected void writeHeader(CSVPrinter printer) throws IOException {
        printer.printRecord((Object[]) new String[] {
                "ID Etablissement",
                "Nom Etablissement",
                "Type d'Etablissement"
        });
    }

    @Override
    protected void writeLine(CSVPrinter printer, ExportEtablissementEditeurDto item) throws IOException {
        List<String> output = writeCommonLine(item.getIdEtablissement(), stripAccents(item.getNomEtablissement().toUpperCase()), stripAccents(item.getTypeEtablissement().toUpperCase()), "", "", "", "", "", "", "", "");
        for (String ip : item.getListeAcces()) {
            output.add(ip);
        }
        printer.printRecord(output);
    }

    @Override
    protected List<ExportEtablissementEditeurDto> getItems(List<ExportEtablissementEditeurDto> etabs) {
        Date dateDernierEnvoi = dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc().orElse(new DateEnvoiEditeurEntity()).getDateEnvoi();
        return mapper.mapList(etabs.stream().filter(e -> {
            Date dateSuppressionEtab = eventService.getDateSuppressionEtab(e.getSirenEtablissement());
            if (dateSuppressionEtab == null) {
                return false;
            } else {
                return dateSuppressionEtab.after(dateDernierEnvoi);
            }
        }).collect(Collectors.toList()), ExportEtablissementEditeurDto.class);
    }
}
